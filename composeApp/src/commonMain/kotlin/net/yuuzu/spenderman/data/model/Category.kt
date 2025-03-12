package net.yuuzu.spenderman.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val color: String,
    val icon: String,
    val isIncome: Boolean = false,
    val budget: Double = 0.0
)
