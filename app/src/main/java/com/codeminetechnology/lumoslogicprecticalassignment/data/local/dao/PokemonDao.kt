package com.codeminetechnology.lumoslogicprecticalassignment.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonEntity
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonListCacheEntity

/**
 * Data Access Object for Pokemon database operations
 */
@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    suspend fun getAllPokemon(): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :pokemonId")
    suspend fun getPokemonById(pokemonId: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE name = :name LIMIT 1")
    suspend fun searchPokemonByName(name: String): PokemonEntity?

    /*@Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' ORDER BY id ASC LIMIT :limit")
    suspend fun searchPokemon(query: String, limit: Int = 20): List<PokemonEntity>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemon(pokemonList: List<PokemonEntity>)

    @Query("DELETE FROM pokemon")
    suspend fun deleteAllPokemon()

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getPokemonCount(): Int

    // Cache operations
    @Query("SELECT * FROM pokemon_list_cache WHERE page = :page")
    suspend fun getCacheByPage(page: Int): PokemonListCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: PokemonListCacheEntity)

    @Query("DELETE FROM pokemon_list_cache WHERE timestamp < :expiryTime")
    suspend fun deleteExpiredCache(expiryTime: Long)
}