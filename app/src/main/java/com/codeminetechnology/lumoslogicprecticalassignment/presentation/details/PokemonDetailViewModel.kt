package com.codeminetechnology.lumoslogicprecticalassignment.presentation.details


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.PokemonDetail
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.NoInternetException
import com.codeminetechnology.lumoslogicprecticalassignment.domain.usecase.GetPokemonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Pokemon detail screen
 */
@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PokemonDetailState>(PokemonDetailState.Loading)
    val state: StateFlow<PokemonDetailState> = _state.asStateFlow()

    /**
     * Load Pokemon details by name
     */
    fun loadPokemonDetail(pokemonName: String) {
        viewModelScope.launch {
            _state.value = PokemonDetailState.Loading
            try {
                val result = getPokemonDetailUseCase(pokemonName)
                result.onSuccess { detail ->
                    _state.value = PokemonDetailState.Success(detail)
                }.onFailure { exception ->
                    _state.value = if (exception is NoInternetException) {
                        PokemonDetailState.NoInternet(exception.message ?: "No internet connection")
                    } else {
                        PokemonDetailState.Error(exception.message ?: "Unknown error")
                    }
                }
            } catch (e: Exception) {
                _state.value = PokemonDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Retry loading when error occurs
     */
    fun retry(pokemonName: String) {
        loadPokemonDetail(pokemonName)
    }
}

/**
 * State sealed class for Pokemon detail screen
 */
sealed class PokemonDetailState {
    object Loading : PokemonDetailState()
    data class Success(val pokemon: PokemonDetail) : PokemonDetailState()
    data class NoInternet(val message: String) : PokemonDetailState()
    data class Error(val message: String) : PokemonDetailState()
}