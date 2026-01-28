package com.wheezy.myjetpackproject.UI.Theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.wheezy.myjetpackproject.Data.Enums.ThemeOption
import com.wheezy.myjetpackproject.R

val LocalTheme = staticCompositionLocalOf { ThemeOption.Auto }

private val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun MyAppTheme(
    themeOption: ThemeOption = ThemeOption.Auto,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeOption) {
        ThemeOption.Auto -> isSystemInDarkTheme()
        ThemeOption.Light -> false
        ThemeOption.Dark -> true
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