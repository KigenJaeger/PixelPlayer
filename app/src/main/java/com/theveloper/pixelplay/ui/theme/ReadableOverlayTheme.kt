package com.theveloper.pixelplay.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private const val ReadableOverlayAlpha = 0.98f

fun ColorScheme.toReadableOverlayColorScheme(): ColorScheme = copy(
    surface = surface.copy(alpha = ReadableOverlayAlpha),
    surfaceBright = surfaceBright.copy(alpha = ReadableOverlayAlpha),
    surfaceDim = surfaceDim.copy(alpha = ReadableOverlayAlpha),
    surfaceContainer = surfaceContainer.copy(alpha = ReadableOverlayAlpha),
    surfaceContainerLow = surfaceContainerLow.copy(alpha = ReadableOverlayAlpha),
    surfaceContainerLowest = surfaceContainerLowest.copy(alpha = ReadableOverlayAlpha),
    surfaceContainerHigh = surfaceContainerHigh.copy(alpha = ReadableOverlayAlpha),
    surfaceContainerHighest = surfaceContainerHighest.copy(alpha = ReadableOverlayAlpha),
    surfaceVariant = surfaceVariant.copy(alpha = ReadableOverlayAlpha),
    primaryContainer = primaryContainer.copy(alpha = ReadableOverlayAlpha),
    secondaryContainer = secondaryContainer.copy(alpha = ReadableOverlayAlpha),
    tertiaryContainer = tertiaryContainer.copy(alpha = ReadableOverlayAlpha)
)

@Composable
fun ReadableOverlayTheme(content: @Composable () -> Unit) {
    val baseColorScheme = MaterialTheme.colorScheme
    val readableColorScheme =
        if (baseColorScheme.background.alpha < 0.1f) {
            baseColorScheme.toReadableOverlayColorScheme()
        } else {
            baseColorScheme
        }

    MaterialTheme(
        colorScheme = readableColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
