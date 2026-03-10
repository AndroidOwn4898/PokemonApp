package com.codeminetechnology.lumoslogicprecticalassignment.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Detailed Pokemon information from PokéAPI
 */
data class PokemonDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("base_experience")
    val baseExperience: Int?,
    @SerializedName("sprites")
    val sprites: SpritesDto,
    @SerializedName("types")
    val types: List<TypeDto>,
    @SerializedName("stats")
    val stats: List<StatDto>
)

data class SpritesDto(
    @SerializedName("front_default")
    val frontDefault: String?,
    @SerializedName("back_default")
    val backDefault: String?
)

data class TypeDto(
    @SerializedName("type")
    val type: TypeInfoDto
)

data class TypeInfoDto(
    @SerializedName("name")
    val name: String
)

data class StatDto(
    @SerializedName("base_stat")
    val baseStat: Int,
    @SerializedName("stat")
    val stat: StatInfoDto
)

data class StatInfoDto(
    @SerializedName("name")
    val name: String
)