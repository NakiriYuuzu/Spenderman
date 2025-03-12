package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import net.yuuzu.spenderman.data.model.Budget

class BudgetDataSource(settings: Settings) : 
    SettingsDataSourceImpl<Budget>(settings, Budget.serializer(), "budget") {
    
    override fun getId(item: Budget): String = item.id
    
    suspend fun getActiveBudgets(date: LocalDate): Flow<List<Budget>> {
        return getAll().map { budgets ->
            budgets.filter { budget ->
                budget.startDate <= date && budget.endDate >= date
            }
        }
    }
    
    suspend fun getBudgetsByCategory(categoryId: String): Flow<List<Budget>> {
        return getAll().map { budgets ->
            budgets.filter { it.categoryId == categoryId }
        }
    }
    
    suspend fun getBudgetsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Budget>> {
        return getAll().map { budgets ->
            budgets.filter { budget ->
                // Budgets that overlap with the given date range
                (budget.startDate <= endDate && budget.endDate >= startDate)
            }
        }
    }
}
