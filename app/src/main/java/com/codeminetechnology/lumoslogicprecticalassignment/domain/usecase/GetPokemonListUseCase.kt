package com.codeminetechnology.lumoslogicprecticalassignment.domain.usecase

import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.PokemonRepository
import javax.inject.Inject

/**
 * Use case for fetching paginated Pokemon list
 * Implements Single Responsibility Principle
 */
class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int = 20
    ): Result<List<Pokemon>> = try {
        val offset = (page - 1) * pageSize
        val result = repository.getPokemonList(limit = pageSize, offset = offset)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}