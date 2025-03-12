package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.yuuzu.spenderman.data.model.DefaultView
import net.yuuzu.spenderman.data.model.Settings as AppSettings
import net.yuuzu.spenderman.data.model.Theme

class SettingsDataSource(private val settings: Settings) {
    private val json = Json { ignoreUnknownKeys = true }
    
    private val KEY_SETTINGS = "app_settings"
    private val KEY_CURRENCY = "currency"
    private val KEY_THEME = "theme"
    private val KEY_LANGUAGE = "language"
    private val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private val KEY_BUDGET_ALERT_THRESHOLD = "budget_alert_threshold"
    private val KEY_DEFAULT_VIEW = "default_view"
    
    suspend fun getSettings(): Flow<AppSettings> = flow {
        val settingsJson = settings.getStringOrNull(KEY_SETTINGS)
        if (settingsJson != null) {
            emit(json.decodeFromString(settingsJson))
        } else {
            val defaultSettings = AppSettings()
            saveSettings(defaultSettings)
            emit(defaultSettings)
        }
    }
    
    suspend fun updateSettings(appSettings: AppSettings): Boolean {
        return try {
            saveSettings(appSettings)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateCurrency(currency: String): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(currency = currency))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateTheme(theme: Theme): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(theme = theme))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateLanguage(language: String): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(language = language))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateNotificationsEnabled(enabled: Boolean): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(notificationsEnabled = enabled))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateBudgetAlertThreshold(threshold: Double): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(budgetAlertThreshold = threshold))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateDefaultView(defaultView: DefaultView): Boolean {
        return try {
            getSettings().collect { currentSettings ->
                saveSettings(currentSettings.copy(defaultView = defaultView))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun resetToDefaults(): Boolean {
        return try {
            saveSettings(AppSettings())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun saveSettings(appSettings: AppSettings) {
        val settingsJson = json.encodeToString(appSettings)
        settings[KEY_SETTINGS] = settingsJson
    }
}
