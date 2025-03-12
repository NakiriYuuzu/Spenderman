package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.DefaultView
import net.yuuzu.spenderman.data.model.Settings
import net.yuuzu.spenderman.data.model.Theme
import net.yuuzu.spenderman.data.repository.SettingsRepository
import net.yuuzu.spenderman.data.source.SettingsDataSource

class SettingsRepositoryImpl(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {
    
    override suspend fun getSettings(): Flow<Settings> {
        return settingsDataSource.getSettings()
    }
    
    override suspend fun updateSettings(settings: Settings): Boolean {
        return settingsDataSource.updateSettings(settings)
    }
    
    override suspend fun updateCurrency(currency: String): Boolean {
        return settingsDataSource.updateCurrency(currency)
    }
    
    override suspend fun updateTheme(theme: Theme): Boolean {
        return settingsDataSource.updateTheme(theme)
    }
    
    override suspend fun updateLanguage(language: String): Boolean {
        return settingsDataSource.updateLanguage(language)
    }
    
    override suspend fun updateNotificationsEnabled(enabled: Boolean): Boolean {
        return settingsDataSource.updateNotificationsEnabled(enabled)
    }
    
    override suspend fun updateBudgetAlertThreshold(threshold: Double): Boolean {
        return settingsDataSource.updateBudgetAlertThreshold(threshold)
    }
    
    override suspend fun resetToDefaults(): Boolean {
        return settingsDataSource.resetToDefaults()
    }
}
