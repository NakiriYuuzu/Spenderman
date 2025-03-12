package net.yuuzu.spenderman.data.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String,
    val amount: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categoryId: String? = null, // If null, it's a general budget
    val name: String = "",
    val description: String = ""
)
