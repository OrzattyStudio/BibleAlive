package com.bible.alive.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Beige50 = Color(0xFFFFF8F0)
private val Beige100 = Color(0xFFFAEBD7)
private val Beige200 = Color(0xFFF5DEB3)
private val Beige300 = Color(0xFFE8D4B8)

private val Brown50 = Color(0xFFEFEBE9)
private val Brown100 = Color(0xFFD7CCC8)
private val Brown200 = Color(0xFFBCAAA4)
private val Brown300 = Color(0xFFA1887F)
private val Brown400 = Color(0xFF8D6E63)
private val Brown500 = Color(0xFF795548)
private val Brown600 = Color(0xFF6D4C41)
private val Brown700 = Color(0xFF5D4037)
private val Brown800 = Color(0xFF4E342E)
private val Brown900 = Color(0xFF3E2723)

private val Terracotta300 = Color(0xFFFF8A65)
private val Terracotta500 = Color(0xFFD4714E)
private val Terracotta600 = Color(0xFFC4633E)

private val BackgroundDark = Color(0xFF1C1410)
private val SurfaceDark = Color(0xFF2C2420)
private val SurfaceVariantDark = Color(0xFF3D3530)

private val LightColorScheme = lightColorScheme(
    primary = Brown500,
    onPrimary = Color.White,
    primaryContainer = Brown100,
    onPrimaryContainer = Brown900,
    secondary = Terracotta500,
    onSecondary = Color.White,
    secondaryContainer = Terracotta300,
    onSecondaryContainer = Brown900,
    tertiary = Color(0xFF8BC34A),
    onTertiary = Color.White,
    background = Beige50,
    onBackground = Brown900,
    surface = Color.White,
    onSurface = Brown900,
    surfaceVariant = Beige100,
    onSurfaceVariant = Brown700,
    outline = Brown300,
    outlineVariant = Beige300
)

private val DarkColorScheme = darkColorScheme(
    primary = Brown100,
    onPrimary = Brown900,
    primaryContainer = Brown700,
    onPrimaryContainer = Brown100,
    secondary = Terracotta300,
    onSecondary = Brown900,
    secondaryContainer = Terracotta600,
    onSecondaryContainer = Beige100,
    tertiary = Color(0xFFAED581),
    onTertiary = Brown900,
    background = BackgroundDark,
    onBackground = Beige100,
    surface = SurfaceDark,
    onSurface = Beige100,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Brown100,
    outline = Brown400,
    outlineVariant = Brown600
)

@Composable
fun BibleAliveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
