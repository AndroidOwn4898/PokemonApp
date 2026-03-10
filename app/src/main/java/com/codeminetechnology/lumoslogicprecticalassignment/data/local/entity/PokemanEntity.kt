package com.codeminetechnology.lumoslogicprecticalassignment.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for storing Pokemon offline
 * Indexed by ID for efficient queries
 */
@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int?,
    val types: String, // Stored as JSON string
    val stats: String, // Stored as JSON string
    val timestamp: Long = System.currentTimeMillis() // For cache invalidation
)

@Entity(tableName = "pokemon_list_cache")
data class PokemonListCacheEntity(
    @PrimaryKey
    val page: Int,
    val data: String, // JSON array of pokemon names
    val timestamp: Long = System.currentTimeMillis()
)