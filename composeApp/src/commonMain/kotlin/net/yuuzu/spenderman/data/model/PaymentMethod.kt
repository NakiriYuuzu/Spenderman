package net.yuuzu.spenderman.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val isDefault: Boolean = false
)
