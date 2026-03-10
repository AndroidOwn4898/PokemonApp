package com.codeminetechnology.lumoslogicprecticalassignment.domain.repository

import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.PokemonDetail


/**
 * Repository interface following Clean Architecture
 * Abstracts data sources from domain layer
 */

interface PokemonRepository {
    /**
     * Get paginated list of Pokemon with offline fallback
     */
    suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon>

    /**
     * Get detailed Pokemon information with offline fallback
     */
    suspend fun getPokemonDetail(nameOrId: String): PokemonDetail

    /**
     * Clear local cache
     */
    suspend fun clearCache()
}