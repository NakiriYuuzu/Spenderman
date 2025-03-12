package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import net.yuuzu.spenderman.data.model.Expense

class ExpenseDataSource(settings: Settings) : 
    SettingsDataSourceImpl<Expense>(settings, Expense.serializer(), "expense") {
    
    override fun getId(item: Expense): String = item.id
    
    suspend fun getByCategory(categoryId: String): Flow<List<Expense>> {
        return getAll().map { expenses ->
            expenses.filter { it.category == categoryId }
        }
    }
    
    suspend fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return getAll().map { expenses ->
            expenses.filter { expense ->
                val expenseDate = expense.date.date
                expenseDate in startDate..endDate
            }
        }
    }
    
    suspend fun getByTags(tags: List<String>): Flow<List<Expense>> {
        return getAll().map { expenses ->
            expenses.filter { expense ->
                expense.tags.any { it in tags }
            }
        }
    }
    
    suspend fun getIncomeExpenses(): Flow<List<Expense>> {
        return getAll().map { expenses ->
            expenses.filter { it.isIncome }
        }
    }
    
    suspend fun getOutcomeExpenses(): Flow<List<Expense>> {
        return getAll().map { expenses ->
            expenses.filter { !it.isIncome }
        }
    }
    
    suspend fun getTotalAmountByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return getByDateRange(startDate, endDate).map { expenses ->
            expenses.sumOf { if (it.isIncome) it.amount else -it.amount }
        }
    }
    
    suspend fun getTotalIncomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return getByDateRange(startDate, endDate).map { expenses ->
            expenses.filter { it.isIncome }.sumOf { it.amount }
        }
    }
    
    suspend fun getTotalOutcomeByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double> {
        return getByDateRange(startDate, endDate).map { expenses ->
            expenses.filter { !it.isIncome }.sumOf { it.amount }
        }
    }
}
