package net.yuuzu.spenderman.ui.viewmodel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.data.model.Expense
import net.yuuzu.spenderman.data.model.PaymentMethod
import net.yuuzu.spenderman.data.model.RecurringType
import net.yuuzu.spenderman.data.model.Tag
import net.yuuzu.spenderman.data.repository.CategoryRepository
import net.yuuzu.spenderman.data.repository.ExpenseRepository
import net.yuuzu.spenderman.data.repository.PaymentMethodRepository
import net.yuuzu.spenderman.data.repository.TagRepository
import kotlin.random.Random

class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val tagRepository: TagRepository
) : BaseViewModel<ExpenseState, ExpenseEvent>(ExpenseState()) {
    
    init {
        loadData()
    }
    
    private fun loadData() {
        val scope = getViewModelScope()
        
        scope.launch {
            categoryRepository.getAll().collectLatest { categories ->
                updateState { it.copy(categories = categories) }
            }
        }
        
        scope.launch {
            paymentMethodRepository.getAll().collectLatest { paymentMethods ->
                updateState { it.copy(paymentMethods = paymentMethods) }
            }
        }
        
        scope.launch {
            tagRepository.getAll().collectLatest { tags ->
                updateState { it.copy(tags = tags) }
            }
        }
    }
    
    override fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.AmountChanged -> {
                updateState { it.copy(amount = event.amount) }
            }
            is ExpenseEvent.CategorySelected -> {
                updateState { it.copy(selectedCategoryId = event.categoryId) }
            }
            is ExpenseEvent.DateChanged -> {
                updateState { it.copy(date = event.date) }
            }
            is ExpenseEvent.DescriptionChanged -> {
                updateState { it.copy(description = event.description) }
            }
            is ExpenseEvent.IsIncomeChanged -> {
                updateState { it.copy(isIncome = event.isIncome) }
            }
            is ExpenseEvent.PaymentMethodSelected -> {
                updateState { it.copy(selectedPaymentMethodId = event.paymentMethodId) }
            }
            is ExpenseEvent.RecurringTypeSelected -> {
                updateState { it.copy(recurringType = event.recurringType) }
            }
            is ExpenseEvent.TagAdded -> {
                val currentTags = state.value.selectedTagIds.toMutableList()
                if (!currentTags.contains(event.tagId)) {
                    currentTags.add(event.tagId)
                    updateState { it.copy(selectedTagIds = currentTags) }
                }
            }
            is ExpenseEvent.TagRemoved -> {
                val currentTags = state.value.selectedTagIds.toMutableList()
                currentTags.remove(event.tagId)
                updateState { it.copy(selectedTagIds = currentTags) }
            }
            is ExpenseEvent.SaveExpense -> {
                saveExpense()
            }
            is ExpenseEvent.LoadExpense -> {
                loadExpense(event.expenseId)
            }
            is ExpenseEvent.ClearForm -> {
                updateState { ExpenseState() }
            }
        }
    }
    
    private fun saveExpense() {
        val scope = getViewModelScope()
        
        scope.launch {
            val currentState = state.value
            
            if (currentState.amount <= 0) {
                updateState { it.copy(error = "Amount must be greater than 0") }
                return@launch
            }
            
            if (currentState.selectedCategoryId.isBlank()) {
                updateState { it.copy(error = "Please select a category") }
                return@launch
            }
            
            val expense = Expense(
                id = currentState.id.ifBlank { generateId() },
                amount = currentState.amount,
                category = currentState.selectedCategoryId,
                description = currentState.description,
                date = currentState.date,
                isIncome = currentState.isIncome,
                tags = currentState.selectedTagIds,
                recurringType = currentState.recurringType,
                paymentMethod = currentState.selectedPaymentMethodId
            )
            
            val success = if (currentState.id.isBlank()) {
                expenseRepository.add(expense)
            } else {
                expenseRepository.update(expense)
            }
            
            if (success) {
                updateState { it.copy(isSaved = true, error = null) }
            } else {
                updateState { it.copy(error = "Failed to save expense") }
            }
        }
    }
    
    private fun loadExpense(expenseId: String) {
        val scope = getViewModelScope()
        
        scope.launch {
            expenseRepository.getById(expenseId).collectLatest { expense ->
                if (expense != null) {
                    updateState {
                        it.copy(
                            id = expense.id,
                            amount = expense.amount,
                            selectedCategoryId = expense.category,
                            description = expense.description,
                            date = expense.date,
                            isIncome = expense.isIncome,
                            selectedTagIds = expense.tags,
                            recurringType = expense.recurringType,
                            selectedPaymentMethodId = expense.paymentMethod
                        )
                    }
                }
            }
        }
    }
    
    private fun generateId(): String {
        return Random.nextInt(100000, 999999).toString()
    }
}

data class ExpenseState(
    val id: String = "",
    val amount: Double = 0.0,
    val selectedCategoryId: String = "",
    val description: String = "",
    val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val isIncome: Boolean = false,
    val selectedTagIds: List<String> = emptyList(),
    val recurringType: RecurringType = RecurringType.NONE,
    val selectedPaymentMethodId: String = "",
    val categories: List<Category> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val isSaved: Boolean = false,
    val error: String? = null
)

sealed class ExpenseEvent {
    data class AmountChanged(val amount: Double) : ExpenseEvent()
    data class CategorySelected(val categoryId: String) : ExpenseEvent()
    data class DescriptionChanged(val description: String) : ExpenseEvent()
    data class DateChanged(val date: LocalDateTime) : ExpenseEvent()
    data class IsIncomeChanged(val isIncome: Boolean) : ExpenseEvent()
    data class TagAdded(val tagId: String) : ExpenseEvent()
    data class TagRemoved(val tagId: String) : ExpenseEvent()
    data class RecurringTypeSelected(val recurringType: RecurringType) : ExpenseEvent()
    data class PaymentMethodSelected(val paymentMethodId: String) : ExpenseEvent()
    data class LoadExpense(val expenseId: String) : ExpenseEvent()
    object SaveExpense : ExpenseEvent()
    object ClearForm : ExpenseEvent()
}
