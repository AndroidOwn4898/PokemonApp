package com.codeminetechnology.lumoslogicprecticalassignment.domain.repository


import android.content.Context
import android.util.Log
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.dao.PokemonDao
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonEntity
import com.codeminetechnology.lumoslogicprecticalassignment.data.remote.PokemonApiService
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.PokemonDetail
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Stat
import com.codeminetechnology.lumoslogicprecticalassignment.util.ConnectivityHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Exception thrown when there is no internet connectivity
 */
class NoInternetException(message: String = "No internet connection") : Exception(message)

/**
 * Repository implementation with offline-first strategy
 * Attempts remote fetch, falls back to local cache
 */
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val gson: Gson,
    private val context: Context,
) : PokemonRepository {

    companion object {
        private const val TAG = "PokemonRepository"
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    override suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> {
        // Clean up expired cache entries on first page load
        if (offset == 0) {
            pokemonDao.deleteExpiredCache(System.currentTimeMillis() - CACHE_EXPIRY_MS)
        }

        if (!ConnectivityHelper.isInternetAvailable(context)) {
            Log.w(TAG, "No internet connection, loading from cache")
            val cached = pokemonDao.getAllPokemon()
            if (cached.isEmpty()) {
                throw NoInternetException("No internet connection and no cached data available")
            }
            return cached.map { entity ->
                Pokemon(id = entity.id, name = entity.name, imageUrl = entity.imageUrl)
            }
        }

        return try {
            val response = apiService.getPokemonList(limit = limit, offset = offset)

            val pokemonList = response.results.mapIndexed { index, dto ->
                val id = (offset + index + 1)
                val imageUrl = buildImageUrl(id)
                Pokemon(id = id, name = dto.name, imageUrl = imageUrl)
            }

            // Cache locally for offline access
            val entities = pokemonList.map { pokemon ->
                PokemonEntity(
                    id = pokemon.id,
                    name = pokemon.name,
                    imageUrl = pokemon.imageUrl,
                    height = 0,
                    weight = 0,
                    baseExperience = null,
                    types = "[]",
                    stats = "[]"
                )
            }
            pokemonDao.insertAllPokemon(entities)

            Log.d(TAG, "Successfully fetched ${pokemonList.size} Pokemon from API")
            pokemonList
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Request timed out: ${e.message}")
            // Fallback to local cache on timeout
            val cached = pokemonDao.getAllPokemon()
            if (cached.isEmpty()) throw NoInternetException("Request timed out and no cached data available")
            cached.map { entity ->
                Pokemon(id = entity.id, name = entity.name, imageUrl = entity.imageUrl)
            }
        } catch (e: Exception) {
            Log.w(TAG, "API call failed, trying local cache: ${e.message}")
            // Fallback to local cache
            pokemonDao.getAllPokemon().map { entity ->
                Pokemon(id = entity.id, name = entity.name, imageUrl = entity.imageUrl)
            }
        }
    }

    override suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        if (!ConnectivityHelper.isInternetAvailable(context)) {
            Log.w(TAG, "No internet connection, loading detail from cache for: $nameOrId")
            return loadDetailFromCache(nameOrId)
                ?: throw NoInternetException("No internet connection and \"$nameOrId\" is not cached")
        }

        return try {
            val response = apiService.getPokemonDetail(nameOrId)

            val types = response.types.map { it.type.name }
            val stats = response.stats.map { Stat(name = it.stat.name, value = it.baseStat) }
            val imageUrl = response.sprites.frontDefault

            val detail = PokemonDetail(
                id = response.id,
                name = response.name,
                imageUrl = imageUrl,
                height = response.height,
                weight = response.weight,
                baseExperience = response.baseExperience,
                types = types,
                stats = stats
            )

            // Cache locally
            pokemonDao.insertPokemon(
                PokemonEntity(
                    id = response.id,
                    name = response.name,
                    imageUrl = imageUrl,
                    height = response.height,
                    weight = response.weight,
                    baseExperience = response.baseExperience,
                    types = gson.toJson(types),
                    stats = gson.toJson(stats)
                )
            )

            Log.d(TAG, "Successfully fetched details for ${response.name}")
            detail
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Request timed out for detail: $nameOrId")
            loadDetailFromCache(nameOrId)
                ?: throw NoInternetException("Request timed out and \"$nameOrId\" is not cached")
        } catch (e: Exception) {
            Log.w(TAG, "API call failed for detail, trying local cache: ${e.message}")
            loadDetailFromCache(nameOrId)
                ?: throw Exception("No cached data available for \"$nameOrId\"")
        }
    }

    override suspend fun clearCache() {
        pokemonDao.deleteAllPokemon()
        Log.d(TAG, "Local cache cleared")
    }

    /**
     * Load Pokemon detail from local cache, searching by name or ID
     */
    private suspend fun loadDetailFromCache(nameOrId: String): PokemonDetail? {
        val cached = nameOrId.toIntOrNull()
            ?.let { pokemonDao.getPokemonById(it) }
            ?: pokemonDao.searchPokemonByName(nameOrId)
            ?: return null

        val types: List<String> = try {
            gson.fromJson(cached.types, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Failed to parse cached types for ${cached.name}: ${e.message}")
            emptyList()
        }

        val stats: List<Stat> = try {
            gson.fromJson(cached.stats, object : TypeToken<List<Stat>>() {}.type) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "Failed to parse cached stats for ${cached.name}: ${e.message}")
            emptyList()
        }

        return PokemonDetail(
            id = cached.id,
            name = cached.name,
            imageUrl = cached.imageUrl,
            height = cached.height,
            weight = cached.weight,
            baseExperience = cached.baseExperience,
            types = types,
            stats = stats
        )
    }

    private fun buildImageUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/pokemon/$pokemonId.png"
    }
}