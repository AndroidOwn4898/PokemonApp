package com.codeminetechnology.lumoslogicprecticalassignment.presentation.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codeminetechnology.lumoslogicprecticalassignment.R
import com.codeminetechnology.lumoslogicprecticalassignment.domain.model.Pokemon
import com.codeminetechnology.lumoslogicprecticalassignment.presentation.components.PokemonImage

/**
 * Pokemon list screen with pagination support
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val pokemonList by viewModel.pokemonList.collectAsState()
    val lazyListState = rememberLazyListState()

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.loadPokemonList()
    }

    // Pagination: Load next page when user scrolls near end
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= (totalItems - 5) && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state == PokemonListState.Success) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pok_mon_list)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        when (state) {
            is PokemonListState.Loading -> {
                if (pokemonList.isEmpty()) {
                    LoadingState(modifier = Modifier.padding(innerPadding))
                } else {
                    PokemonListContent(
                        pokemonList = pokemonList,
                        onPokemonClick = onPokemonClick,
                        lazyListState = lazyListState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            is PokemonListState.Success -> {
                PokemonListContent(
                    pokemonList = pokemonList,
                    onPokemonClick = onPokemonClick,
                    lazyListState = lazyListState,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is PokemonListState.Empty -> {
                EmptyState(modifier = Modifier.padding(innerPadding))
            }

            is PokemonListState.NoInternet -> {
                NoInternetState(
                    message = (state as PokemonListState.NoInternet).message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is PokemonListState.Error -> {
                ErrorState(
                    message = (state as PokemonListState.Error).message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is PokemonListState.Idle -> {
                // Initial state
            }
        }
    }
}

@Composable
private fun PokemonListContent(
    pokemonList: List<Pokemon>,
    onPokemonClick: (String) -> Unit,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            PokemonCard(
                pokemon = pokemon,
                onPokemonClick = onPokemonClick
            )
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: Pokemon,
    onPokemonClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPokemonClick(pokemon.name) }
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PokemonImage(
                imageUrl = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}

@Composable
private fun NoInternetState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "No internet",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            androidx.compose.material3.Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_pok_mon_found),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            androidx.compose.material3.Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}