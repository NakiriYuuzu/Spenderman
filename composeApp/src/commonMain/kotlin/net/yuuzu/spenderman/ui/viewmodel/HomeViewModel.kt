package net.yuuzu.spenderman.ui.viewmodel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.yuuzu.spenderman.util.getDaysInMonth
import net.yuuzu.spenderman.data.model.Expense
import net.yuuzu.spenderman.data.repository.BudgetRepository
import net.yuuzu.spenderman.data.repository.CategoryRepository
import net.yuuzu.spenderman.data.repository.ExpenseRepository
import net.yuuzu.spenderman.data.repository.SettingsRepository

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    
    init {
        loadData()
    }
    
    private fun loadData() {
        val scope = getViewModelScope()
        
        scope.launch {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val startOfMonth = LocalDate(today.year, today.month, 1)
            val endOfMonth = LocalDate(
                today.year,
                today.month,
                getDaysInMonth(today.month, today.year)
            )
            
            // Load total income and expenses for the current month
            expenseRepository.getTotalIncomeByDateRange(startOfMonth, endOfMonth).collectLatest { income ->
                updateState { it.copy(totalIncome = income) }
            }
        }
        
        scope.launch {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val startOfMonth = LocalDate(today.year, today.month, 1)
            val endOfMonth = LocalDate(
                today.year,
                today.month,
                getDaysInMonth(today.month, today.year)
            )
            
            expenseRepository.getTotalOutcomeByDateRange(startOfMonth, endOfMonth).collectLatest { expense ->
                updateState { it.copy(totalExpense = expense) }
            }
        }
        
        scope.launch {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            // Load active budgets
            budgetRepository.getActiveBudgets(today).collectLatest { budgets ->
                updateState { it.copy(activeBudgets = budgets) }
                
                // Calculate progress for each budget
                val progressMap = mutableMapOf<String, Float>()
                budgets.forEach { budget ->
                    scope.launch {
                        budgetRepository.getBudgetProgress(budget.id).collectLatest { progress ->
                            progressMap[budget.id] = progress.toFloat()
                            updateState { it.copy(budgetProgress = progressMap.toMap()) }
                        }
                    }
                }
            }
        }
        
        scope.launch {
            // Load recent transactions
            expenseRepository.getAll().collectLatest { expenses ->
                val sortedExpenses = expenses.sortedByDescending { it.date }
                updateState { it.copy(recentTransactions = sortedExpenses.take(5)) }
            }
        }
        
        scope.launch {
            // Load categories
            categoryRepository.getAll().collectLatest { categories ->
                updateState { it.copy(categories = categories) }
            }
        }
    }
    
    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadData()
        }
    }
}

data class HomeState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val activeBudgets: List<net.yuuzu.spenderman.data.model.Budget> = emptyList(),
    val budgetProgress: Map<String, Float> = emptyMap(), // Map of budget ID to progress
    val recentTransactions: List<Expense> = emptyList(),
    val categories: List<net.yuuzu.spenderman.data.model.Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HomeEvent {
    object Refresh : HomeEvent()
}
