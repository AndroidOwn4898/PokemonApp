package com.codeminetechnology.lumoslogicprecticalassignment.domain.model


/**
 * Domain model for Pokemon list item
 * Independent of data layer representation
 */
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?
)

/**
 * Domain model for detailed Pokemon information
 */
data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int?,
    val types: List<String>,
    val stats: List<Stat>
)

data class Stat(
    val name: String,
    val value: Int
)