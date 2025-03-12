package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.Settings
import net.yuuzu.spenderman.data.model.Theme

interface SettingsRepository {
    suspend fun getSettings(): Flow<Settings>
    suspend fun updateSettings(settings: Settings): Boolean
    suspend fun updateCurrency(currency: String): Boolean
    suspend fun updateTheme(theme: Theme): Boolean
    suspend fun updateLanguage(language: String): Boolean
    suspend fun updateNotificationsEnabled(enabled: Boolean): Boolean
    suspend fun updateBudgetAlertThreshold(threshold: Double): Boolean
    suspend fun resetToDefaults(): Boolean
}
