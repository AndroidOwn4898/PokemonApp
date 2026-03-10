package com.codeminetechnology.lumoslogicprecticalassignment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Reusable Pokemon image composable that handles loading and error states
 * using Material Design 3 icons instead of drawable placeholders.
 *
 * - Loading: gray background with semi-transparent image icon
 * - Success: Pokemon image displayed normally with crossfade
 * - Error / 404: red background with red image icon
 * - No URL: gray background with semi-transparent image icon
 */
@Composable
fun PokemonImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    var isLoading by remember(imageUrl) { mutableStateOf(true) }
    var isError by remember(imageUrl) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(
                color = if (isError)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit,
            onLoading = { isLoading = true },
            onSuccess = {
                isLoading = false
                isError = false
            },
            onError = {
                isLoading = false
                isError = true
            },
        )

        if (isLoading || isError || imageUrl.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = if (isError) "Image Not Found" else "Loading",
                modifier = Modifier
                    .size(48.dp)
                    .alpha(0.5f),
                tint = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
