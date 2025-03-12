package net.yuuzu.spenderman.ui.viewmodel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.yuuzu.spenderman.data.model.DefaultView
import net.yuuzu.spenderman.data.model.Settings
import net.yuuzu.spenderman.data.model.Theme
import net.yuuzu.spenderman.data.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SettingsState, SettingsEvent>(SettingsState()) {
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        val scope = getViewModelScope()
        
        scope.launch {
            settingsRepository.getSettings().collectLatest { settings ->
                updateState {
                    it.copy(
                        currency = settings.currency,
                        theme = settings.theme,
                        language = settings.language,
                        notificationsEnabled = settings.notificationsEnabled,
                        budgetAlertThreshold = settings.budgetAlertThreshold,
                        defaultView = settings.defaultView
                    )
                }
            }
        }
    }
    
    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.CurrencyChanged -> {
                updateState { it.copy(currency = event.currency) }
                saveSettings()
            }
            is SettingsEvent.ThemeChanged -> {
                updateState { it.copy(theme = event.theme) }
                saveSettings()
            }
            is SettingsEvent.LanguageChanged -> {
                updateState { it.copy(language = event.language) }
                saveSettings()
            }
            is SettingsEvent.NotificationsEnabledChanged -> {
                updateState { it.copy(notificationsEnabled = event.enabled) }
                saveSettings()
            }
            is SettingsEvent.BudgetAlertThresholdChanged -> {
                updateState { it.copy(budgetAlertThreshold = event.threshold) }
                saveSettings()
            }
            is SettingsEvent.DefaultViewChanged -> {
                updateState { it.copy(defaultView = event.defaultView) }
                saveSettings()
            }
            is SettingsEvent.ResetToDefaults -> {
                resetToDefaults()
            }
        }
    }
    
    private fun saveSettings() {
        val scope = getViewModelScope()
        
        scope.launch {
            val currentState = state.value
            
            val settings = Settings(
                currency = currentState.currency,
                theme = currentState.theme,
                language = currentState.language,
                notificationsEnabled = currentState.notificationsEnabled,
                budgetAlertThreshold = currentState.budgetAlertThreshold,
                defaultView = currentState.defaultView
            )
            
            val success = settingsRepository.updateSettings(settings)
            
            if (!success) {
                updateState { it.copy(error = "Failed to save settings") }
            } else {
                updateState { it.copy(error = null) }
            }
        }
    }
    
    private fun resetToDefaults() {
        val scope = getViewModelScope()
        
        scope.launch {
            val success = settingsRepository.resetToDefaults()
            
            if (success) {
                loadSettings()
            } else {
                updateState { it.copy(error = "Failed to reset settings") }
            }
        }
    }
}

data class SettingsState(
    val currency: String = "USD",
    val theme: Theme = Theme.SYSTEM,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val budgetAlertThreshold: Double = 0.8,
    val defaultView: DefaultView = DefaultView.MONTHLY,
    val error: String? = null
)

sealed class SettingsEvent {
    data class CurrencyChanged(val currency: String) : SettingsEvent()
    data class ThemeChanged(val theme: Theme) : SettingsEvent()
    data class LanguageChanged(val language: String) : SettingsEvent()
    data class NotificationsEnabledChanged(val enabled: Boolean) : SettingsEvent()
    data class BudgetAlertThresholdChanged(val threshold: Double) : SettingsEvent()
    data class DefaultViewChanged(val defaultView: DefaultView) : SettingsEvent()
    object ResetToDefaults : SettingsEvent()
}
