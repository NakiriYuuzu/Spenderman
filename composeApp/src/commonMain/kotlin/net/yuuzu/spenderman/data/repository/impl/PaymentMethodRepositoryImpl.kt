package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.PaymentMethod
import net.yuuzu.spenderman.data.repository.PaymentMethodRepository
import net.yuuzu.spenderman.data.source.PaymentMethodDataSource

class PaymentMethodRepositoryImpl(
    private val paymentMethodDataSource: PaymentMethodDataSource
) : PaymentMethodRepository {
    
    override suspend fun getAll(): Flow<List<PaymentMethod>> {
        return paymentMethodDataSource.getAll()
    }
    
    override suspend fun getById(id: String): Flow<PaymentMethod?> {
        return paymentMethodDataSource.getById(id)
    }
    
    override suspend fun add(item: PaymentMethod): Boolean {
        return paymentMethodDataSource.add(item)
    }
    
    override suspend fun update(item: PaymentMethod): Boolean {
        return paymentMethodDataSource.update(item)
    }
    
    override suspend fun delete(id: String): Boolean {
        return paymentMethodDataSource.delete(id)
    }
    
    override suspend fun deleteAll(): Boolean {
        return paymentMethodDataSource.deleteAll()
    }
    
    override suspend fun getDefaultPaymentMethod(): Flow<PaymentMethod?> {
        return paymentMethodDataSource.getDefaultPaymentMethod()
    }
    
    override suspend fun setDefaultPaymentMethod(id: String): Boolean {
        return paymentMethodDataSource.setDefaultPaymentMethod(id)
    }
}
