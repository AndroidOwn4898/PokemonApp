package com.codeminetechnology.lumoslogicprecticalassignment.presentation.list


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.NoInternetException
import com.codeminetechnology.lumoslogicprecticalassignment.domain.usecase.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Pokemon list screen
 * Manages state and business logic for pagination
 */
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<PokemonListState>(PokemonListState.Idle)
    val state: StateFlow<PokemonListState> = _state.asStateFlow()

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    /**
     * Load Pokemon list with pagination
     */
    fun loadPokemonList() {
        viewModelScope.launch {
            _state.value = PokemonListState.Loading
            try {
                val result = getPokemonListUseCase(page = currentPage, pageSize = pageSize)
                result.onSuccess { pokemon ->
                    Log.d("Pokemon", pokemon.toString())
                    if (currentPage == 1) {
                        _pokemonList.value = pokemon
                    } else {
                        _pokemonList.value = _pokemonList.value + pokemon
                    }
                    _state.value = if (pokemon.isEmpty()) {
                        PokemonListState.Empty
                    } else {
                        PokemonListState.Success
                    }
                }.onFailure { exception ->
                    _state.value = if (exception is NoInternetException) {
                        PokemonListState.NoInternet(exception.message ?: "No internet connection")
                    } else {
                        PokemonListState.Error(exception.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _state.value = PokemonListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Load next page (pagination)
     */
    fun loadNextPage() {
        currentPage++
        loadPokemonList()
    }

    /**
     * Retry loading when error occurs
     */
    fun retry() {
        loadPokemonList()
    }

    /**
     * Refresh Pokemon list (reset pagination)
     */
    fun refresh() {
        currentPage = 1
        loadPokemonList()
    }
}

/**
 * State sealed class for Pokemon list screen
 */
sealed class PokemonListState {
    object Idle : PokemonListState()
    object Loading : PokemonListState()
    object Success : PokemonListState()
    object Empty : PokemonListState()
    data class NoInternet(val message: String) : PokemonListState()
    data class Error(val message: String) : PokemonListState()
}