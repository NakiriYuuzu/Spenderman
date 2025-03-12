package net.yuuzu.spenderman.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: LocalDateTime,
    val isIncome: Boolean = false,
    val tags: List<String> = emptyList(),
    val recurringType: RecurringType = RecurringType.NONE,
    val paymentMethod: String = ""
)

enum class RecurringType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
