package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.PaymentMethod

interface PaymentMethodRepository : Repository<PaymentMethod> {
    suspend fun getDefaultPaymentMethod(): Flow<PaymentMethod?>
    suspend fun setDefaultPaymentMethod(id: String): Boolean
}
