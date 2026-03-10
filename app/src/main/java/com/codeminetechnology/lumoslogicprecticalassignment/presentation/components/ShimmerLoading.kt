package com.codeminetechnology.lumoslogicprecticalassignment.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

/**
 * Premium Shimmer Loading Animation Component
 * Shows smooth, realistic shimmer effect while content loads
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    highlightColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
) {
    val shimmerColors = listOf(
        baseColor,
        highlightColor,
        baseColor,
    )

    val transition = rememberInfiniteTransition(label = "shimmer_animation")

    val translateAnim = transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing,
                delayMillis = 300
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value, 0f),
        end = Offset(translateAnim.value + 300f, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = brush,
                shape = RoundedCornerShape(12.dp)
            )
    )
}

/**
 * Shimmer Card Placeholder - for list items
 * Shows shimmer effect in card format
 */
@Composable
fun ShimmerCardItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Title shimmer
        ShimmerLoading(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .padding(top = 12.dp, start = 12.dp)
        )

        // Image shimmer (main content)
        ShimmerLoading(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(12.dp)
        )
    }
}

/**
 * Shimmer Detail Placeholder - for detail screens
 */
@Composable
fun ShimmerDetailItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Header shimmer
        ShimmerLoading(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        // Text shimmer lines
        repeat(3) { index ->
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .padding(top = (160 + index * 30).dp, start = 16.dp)
            )
        }
    }
}