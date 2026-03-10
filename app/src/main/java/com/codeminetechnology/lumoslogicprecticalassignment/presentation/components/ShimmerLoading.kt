package com.codeminetechnology.lumoslogicprecticalassignment.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val SHIMMER_TRANSLATE_DISTANCE = 400f
private const val SHIMMER_WIDTH = 100f

/**
 * Shimmer loading animation component.
 * Shows a smooth animated gradient while content is loading,
 * providing visual feedback that mimics the card dimensions.
 *
 * @param modifier Modifier to apply to the shimmer box
 * @param baseColor Base background color of the shimmer
 * @param highlightColor Highlight sweep color of the shimmer
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
) {
    val shimmerColors = listOf(
        baseColor,
        highlightColor,
        baseColor,
    )

    val transition = rememberInfiniteTransition(label = "shimmer_animation")

    val position = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_position",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(position.value * SHIMMER_TRANSLATE_DISTANCE, 0f),
        end = Offset(position.value * SHIMMER_TRANSLATE_DISTANCE + SHIMMER_WIDTH, 0f),
    )

    Box(
        modifier = modifier
            .background(brush = brush, shape = RoundedCornerShape(12.dp)),
    )
}
