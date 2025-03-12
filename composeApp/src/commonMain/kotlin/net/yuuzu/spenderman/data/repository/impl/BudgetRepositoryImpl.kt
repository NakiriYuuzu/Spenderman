package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import net.yuuzu.spenderman.data.model.Budget
import net.yuuzu.spenderman.data.repository.BudgetRepository
import net.yuuzu.spenderman.data.source.BudgetDataSource
import net.yuuzu.spenderman.data.source.ExpenseDataSource

class BudgetRepositoryImpl(
    private val budgetDataSource: BudgetDataSource,
    private val expenseDataSource: ExpenseDataSource
) : BudgetRepository {
    
    override suspend fun getAll(): Flow<List<Budget>> {
        return budgetDataSource.getAll()
    }
    
    override suspend fun getById(id: String): Flow<Budget?> {
        return budgetDataSource.getById(id)
    }
    
    override suspend fun add(item: Budget): Boolean {
        return budgetDataSource.add(item)
    }
    
    override suspend fun update(item: Budget): Boolean {
        return budgetDataSource.update(item)
    }
    
    override suspend fun delete(id: String): Boolean {
        return budgetDataSource.delete(id)
    }
    
    override suspend fun deleteAll(): Boolean {
        return budgetDataSource.deleteAll()
    }
    
    override suspend fun getActiveBudgets(date: LocalDate): Flow<List<Budget>> {
        return budgetDataSource.getActiveBudgets(date)
    }
    
    override suspend fun getBudgetsByCategory(categoryId: String): Flow<List<Budget>> {
        return budgetDataSource.getBudgetsByCategory(categoryId)
    }
    
    override suspend fun getBudgetsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Budget>> {
        return budgetDataSource.getBudgetsByDateRange(startDate, endDate)
    }
    
    override suspend fun getBudgetProgress(budgetId: String): Flow<Double> {
        return budgetDataSource.getById(budgetId).map { budget ->
            if (budget == null) {
                0.0
            } else {
                val expenses = if (budget.categoryId != null) {
                    expenseDataSource.getByCategory(budget.categoryId).firstOrNull() ?: emptyList()
                } else {
                    expenseDataSource.getByDateRange(budget.startDate, budget.endDate).firstOrNull() ?: emptyList()
                }
                
                val totalExpense = expenses
                    .filter { !it.isIncome }
                    .sumOf { it.amount }
                
                if (budget.amount <= 0.0) {
                    0.0
                } else {
                    (totalExpense / budget.amount).coerceIn(0.0, 1.0)
                }
            }
        }
    }
}
