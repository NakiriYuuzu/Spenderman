package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.yuuzu.spenderman.data.model.PaymentMethod

class PaymentMethodDataSource(settings: Settings) : 
    SettingsDataSourceImpl<PaymentMethod>(settings, PaymentMethod.serializer(), "payment_method") {
    
    override fun getId(item: PaymentMethod): String = item.id
    
    suspend fun getDefaultPaymentMethod(): Flow<PaymentMethod?> {
        return getAll().map { paymentMethods ->
            paymentMethods.find { it.isDefault }
        }
    }
    
    suspend fun setDefaultPaymentMethod(id: String): Boolean {
        return try {
            val allPaymentMethods = mutableListOf<PaymentMethod>()
            getAll().collect { paymentMethods ->
                allPaymentMethods.addAll(paymentMethods)
            }
            
            // Update all payment methods
            allPaymentMethods.forEach { paymentMethod ->
                val updated = paymentMethod.copy(isDefault = paymentMethod.id == id)
                update(updated)
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}
