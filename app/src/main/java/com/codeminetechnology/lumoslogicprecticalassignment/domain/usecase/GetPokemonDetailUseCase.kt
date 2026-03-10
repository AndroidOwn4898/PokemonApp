package com.codeminetechnology.lumoslogicprecticalassignment.domain.usecase

import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.PokemonDetail
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.PokemonRepository
import javax.inject.Inject

/**
 * Use case for fetching detailed Pokemon information
 */
class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(pokemonName: String): Result<PokemonDetail> = try {
        val result = repository.getPokemonDetail(pokemonName)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}