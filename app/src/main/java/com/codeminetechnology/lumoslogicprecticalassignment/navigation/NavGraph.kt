package com.codeminetechnology.lumoslogicprecticalassignment.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.codeminetechnology.lumoslogicprecticalassignment.presentation.details.PokemonDetailScreen
import com.codeminetechnology.lumoslogicprecticalassignment.presentation.list.PokemonListScreen

/**
 * Navigation graph defining app routes and transitions
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.POKEMON_LIST
    ) {
        composable(route = Route.POKEMON_LIST) {
            PokemonListScreen(
                onPokemonClick = { pokemonName ->
                    navController.navigate(Route.pokemonDetail(pokemonName))
                }
            )
        }

        composable(
            route = Route.POKEMON_DETAIL_WITH_ARGS,
            arguments = listOf(
                androidx.navigation.navArgument(Route.ARG_POKEMON_NAME) {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString(Route.ARG_POKEMON_NAME) ?: ""
            PokemonDetailScreen(
                pokemonName = pokemonName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Route definitions
 */
object Route {
    const val POKEMON_LIST = "pokemon_list"
    const val ARG_POKEMON_NAME = "pokemon_name"
    const val POKEMON_DETAIL_WITH_ARGS = "pokemon_detail/{$ARG_POKEMON_NAME}"

    fun pokemonDetail(pokemonName: String) = "pokemon_detail/$pokemonName"
}