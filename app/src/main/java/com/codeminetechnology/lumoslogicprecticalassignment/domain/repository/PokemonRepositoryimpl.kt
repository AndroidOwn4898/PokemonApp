package com.codeminetechnology.lumoslogicprecticalassignment.domain.repository


import android.util.Log
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.dao.PokemonDao
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonEntity
import com.codeminetechnology.lumoslogicprecticalassignment.data.remote.PokemonApiService
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.PokemonDetail
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Stat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/**
 * Repository implementation with offline-first strategy
 * Attempts remote fetch, falls back to local cache
 */
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val gson: Gson,
) : PokemonRepository {

    companion object {
        private const val TAG = "PokemonRepository"
        private const val CACHE_EXPIRY_HOURS = 24
    }

    override suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return try {
            // Try remote API first
            val response = apiService.getPokemonList(limit = limit, offset = offset)

            // Extract ID from URL for better offline data
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
        } catch (e: Exception) {
            Log.w(TAG, "API call failed, trying local cache: ${e.message}")
            // Fallback to local cache
            pokemonDao.getAllPokemon().map { entity ->
                Pokemon(id = entity.id, name = entity.name, imageUrl = entity.imageUrl)
            }
        }
    }

    override suspend fun getPokemonDetail(nameOrId: String): PokemonDetail {
        return try {
            // Try remote API first
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
        } catch (e: Exception) {
            Log.w(TAG, "API call failed for detail, trying local cache: ${e.message}")
            // Fallback to local cache
            val cached = pokemonDao.getPokemonById(nameOrId.toIntOrNull() ?: 1)
                ?: throw Exception("No cached data available")

            val types: List<String> = try {
                gson.fromJson(cached.types, object : TypeToken<List<String>>() {}.type)
            } catch (e: Exception) {
                emptyList()
            }

            val stats: List<Stat> = try {
                gson.fromJson(cached.stats, object : TypeToken<List<Stat>>() {}.type)
            } catch (e: Exception) {
                emptyList()
            }

            PokemonDetail(
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
    }

    override suspend fun clearCache() {
        pokemonDao.deleteAllPokemon()
        Log.d(TAG, "Local cache cleared")
    }

    private fun buildImageUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/pokemon/$pokemonId.png"
    }
}