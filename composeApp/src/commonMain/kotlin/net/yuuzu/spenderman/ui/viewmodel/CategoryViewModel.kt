package net.yuuzu.spenderman.ui.viewmodel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.data.repository.CategoryRepository

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : BaseViewModel<CategoryState, CategoryEvent>(CategoryState()) {
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        val scope = getViewModelScope()
        
        updateState { it.copy(isLoading = true) }
        
        scope.launch {
            categoryRepository.getAll().collectLatest { categories ->
                updateState { 
                    it.copy(
                        categories = categories,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    override fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.AddCategory -> addCategory(event.category)
            is CategoryEvent.UpdateCategory -> updateCategory(event.category)
            is CategoryEvent.DeleteCategory -> deleteCategory(event.categoryId)
            is CategoryEvent.ToggleIncomeFilter -> updateState { 
                it.copy(showIncomeCategories = event.showIncome) 
            }
        }
    }
    
    private fun addCategory(category: Category) {
        val scope = getViewModelScope()
        
        scope.launch {
            val success = categoryRepository.add(category)
            
            if (!success) {
                updateState { it.copy(error = "Failed to add category") }
            } else {
                updateState { it.copy(error = null) }
                // Force refresh categories after adding
                loadCategories()
            }
        }
    }
    
    private fun updateCategory(category: Category) {
        val scope = getViewModelScope()
        
        scope.launch {
            val success = categoryRepository.update(category)
            
            if (!success) {
                updateState { it.copy(error = "Failed to update category") }
            } else {
                updateState { it.copy(error = null) }
                // Force refresh categories after updating
                loadCategories()
            }
        }
    }
    
    private fun deleteCategory(categoryId: String) {
        val scope = getViewModelScope()
        
        scope.launch {
            val success = categoryRepository.delete(categoryId)
            
            if (!success) {
                updateState { it.copy(error = "Failed to delete category") }
            } else {
                updateState { it.copy(error = null) }
                // Force refresh categories after deleting
                loadCategories()
            }
        }
    }
}

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val showIncomeCategories: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CategoryEvent {
    data class AddCategory(val category: Category) : CategoryEvent()
    data class UpdateCategory(val category: Category) : CategoryEvent()
    data class DeleteCategory(val categoryId: String) : CategoryEvent()
    data class ToggleIncomeFilter(val showIncome: Boolean) : CategoryEvent()
}
