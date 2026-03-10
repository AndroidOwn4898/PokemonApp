package com.codeminetechnology.lumoslogicprecticalassignment.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.dao.PokemonDao
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonEntity
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity.PokemonListCacheEntity

/**
 * Room database for Pokemon offline support
 * Version 1: Initial schema with pokemon and cache tables
 */
@Database(
    entities = [PokemonEntity::class, PokemonListCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}