package com.wheezy.myjetpackproject.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalTheme = staticCompositionLocalOf { com.wheezy.myjetpackproject.core.model.ThemeOption.Auto }

private val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun MyAppTheme(
    themeOption: com.wheezy.myjetpackproject.core.model.ThemeOption = com.wheezy.myjetpackproject.core.model.ThemeOption.Auto,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeOption) {
        com.wheezy.myjetpackproject.core.model.ThemeOption.Auto -> isSystemInDarkTheme()
        com.wheezy.myjetpackproject.core.model.ThemeOption.Light -> false
        com.wheezy.myjetpackproject.core.model.ThemeOption.Dark -> true
    }

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF7B3EFF),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF311B92),

            secondary = Color(0xFFE91E63),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF880E4F),

            tertiary = Color(0xFFFF9800),
            onTertiary = Color.Black,
            tertiaryContainer = Color(0xFFE65100),

            background = Color(0xFF121212),
            onBackground = Color.White,

            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF2D2D2D),
            onSurfaceVariant = Color(0xFFB0B0B0),

            error = Color(0xFFF44336),
            outline = Color(0xFF666666)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF7B3EFF),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFF3E9FF),

            secondary = Color(0xFFE91E63),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFCE4EC),

            tertiary = Color(0xFFFF9800),
            onTertiary = Color(0xFF212121),
            tertiaryContainer = Color(0xFFFFF3E0),

            background = Color.White,
            onBackground = Color(0xFF212121),

            surface = Color(0xFFF8F9FA),
            onSurface = Color(0xFF212121),
            surfaceVariant = Color(0xFFE9ECEF),
            onSurfaceVariant = Color(0xFF666666),

            error = Color(0xFFF44336),
            outline = Color(0xFFDDDDDD)
        )
    }

    CompositionLocalProvider(
        LocalTheme provides themeOption
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}