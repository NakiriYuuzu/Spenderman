package net.yuuzu.spenderman

import androidx.compose.runtime.*
import net.yuuzu.spenderman.di.initKoin
import net.yuuzu.spenderman.ui.navigation.Navigation
import net.yuuzu.spenderman.ui.theme.AppTheme
import net.yuuzu.spenderman.ui.theme.DefaultSeedColor
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import net.yuuzu.spenderman.ui.viewmodel.SettingsViewModel

// Initialize Koin
private val koin = initKoin().koin

@Composable
@Preview
fun App() {
    KoinContext {
        // Get the current theme from settings
        val settingsViewModel = koinInject<SettingsViewModel>()
        val settingsState by settingsViewModel.state.collectAsState()
        val currentTheme = settingsState.theme

        AppTheme(
            theme = currentTheme,
            seedColor = DefaultSeedColor
        ) {
            Navigation()
        }
    }
}
