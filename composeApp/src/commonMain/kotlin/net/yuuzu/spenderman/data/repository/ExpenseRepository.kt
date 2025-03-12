package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.yuuzu.spenderman.data.model.Expense

interface ExpenseRepository : Repository<Expense> {
    suspend fun getByCategory(categoryId: String): Flow<List<Expense>>
    suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    suspend fun getByTags(tags: List<String>): Flow<List<Expense>>
    suspend fun getIncomeExpenses(): Flow<List<Expense>>
    suspend fun getOutcomeExpenses(): Flow<List<Expense>>
    suspend fun getTotalAmountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>
    suspend fun getTotalIncomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>
    suspend fun getTotalOutcomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double>
}
