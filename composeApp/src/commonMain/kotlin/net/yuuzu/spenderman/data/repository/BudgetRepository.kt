package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.yuuzu.spenderman.data.model.Budget

interface BudgetRepository : Repository<Budget> {
    suspend fun getActiveBudgets(date: LocalDate): Flow<List<Budget>>
    suspend fun getBudgetsByCategory(categoryId: String): Flow<List<Budget>>
    suspend fun getBudgetsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Budget>>
    suspend fun getBudgetProgress(budgetId: String): Flow<Double> // Returns percentage of budget used
}
