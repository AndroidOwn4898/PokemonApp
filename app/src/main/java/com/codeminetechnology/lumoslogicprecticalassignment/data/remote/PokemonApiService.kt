package com.codeminetechnology.lumoslogicprecticalassignment.data.remote

import com.codeminetechnology.lumoslogicprecticalassignment.data.remote.dto.PokemonDetailResponse
import com.codeminetechnology.lumoslogicprecticalassignment.data.remote.dto.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service for PokéAPI
 * Base URL: https://pokeapi.co/api/v2/
 */
interface PokemonApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): PokemonListResponse

    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(
        @Path("nameOrId") nameOrId: String,
    ): PokemonDetailResponse
}