package net.yuuzu.spenderman.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import net.yuuzu.spenderman.data.model.Theme
import net.yuuzu.spenderman.util.LocalScreenSize
import net.yuuzu.spenderman.util.ScreenSize
import net.yuuzu.spenderman.util.getScreenHeightDp
import net.yuuzu.spenderman.util.getScreenWidthDp

// Default seed color - a nice blue shade
val DefaultSeedColor = Color(0xFF1E88E5)

// Composition local for theme
val LocalAppTheme = compositionLocalOf { Theme.SYSTEM }

@Composable
fun AppTheme(
    theme: Theme = Theme.SYSTEM,
    seedColor: Color = DefaultSeedColor,
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Generate dynamic color scheme using material-kolor
    val colorScheme = dynamicColorScheme(
        primary = seedColor,
        isDark = darkTheme,
        isAmoled = false,
        style = PaletteStyle.TonalSpot
    )
    
    CompositionLocalProvider(
        LocalAppTheme provides theme,
        LocalScreenSize provides ScreenSize(getScreenWidthDp(), getScreenHeightDp())
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    content()
                }
            }
        )
    }
}
