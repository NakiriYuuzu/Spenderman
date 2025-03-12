package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.yuuzu.spenderman.data.model.Expense
import net.yuuzu.spenderman.data.repository.ExpenseRepository
import net.yuuzu.spenderman.data.source.ExpenseDataSource

class ExpenseRepositoryImpl(
    private val expenseDataSource: ExpenseDataSource
) : ExpenseRepository {
    
    override suspend fun getAll(): Flow<List<Expense>> {
        return expenseDataSource.getAll()
    }
    
    override suspend fun getById(id: String): Flow<Expense?> {
        return expenseDataSource.getById(id)
    }
    
    override suspend fun add(item: Expense): Boolean {
        return expenseDataSource.add(item)
    }
    
    override suspend fun update(item: Expense): Boolean {
        return expenseDataSource.update(item)
    }
    
    override suspend fun delete(id: String): Boolean {
        return expenseDataSource.delete(id)
    }
    
    override suspend fun deleteAll(): Boolean {
        return expenseDataSource.deleteAll()
    }
    
    override suspend fun getByCategory(categoryId: String): Flow<List<Expense>> {
        return expenseDataSource.getByCategory(categoryId)
    }
    
    override suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return expenseDataSource.getByDateRange(startDate, endDate)
    }
    
    override suspend fun getByTags(tags: List<String>): Flow<List<Expense>> {
        return expenseDataSource.getByTags(tags)
    }
    
    override suspend fun getIncomeExpenses(): Flow<List<Expense>> {
        return expenseDataSource.getIncomeExpenses()
    }
    
    override suspend fun getOutcomeExpenses(): Flow<List<Expense>> {
        return expenseDataSource.getOutcomeExpenses()
    }
    
    override suspend fun getTotalAmountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return expenseDataSource.getTotalAmountByDateRange(startDate, endDate)
    }
    
    override suspend fun getTotalIncomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return expenseDataSource.getTotalIncomeByDateRange(startDate, endDate)
    }
    
    override suspend fun getTotalOutcomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return expenseDataSource.getTotalOutcomeByDateRange(startDate, endDate)
    }
}
