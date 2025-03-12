package net.yuuzu.spenderman.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val currency: String = "USD",
    val theme: Theme = Theme.SYSTEM,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val budgetAlertThreshold: Double = 0.8, // Alert when 80% of budget is used
    val defaultView: DefaultView = DefaultView.MONTHLY
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}

enum class DefaultView {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
