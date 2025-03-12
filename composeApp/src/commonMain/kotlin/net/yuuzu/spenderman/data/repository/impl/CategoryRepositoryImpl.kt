package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.yuuzu.spenderman.data.model.Category
import net.yuuzu.spenderman.data.repository.CategoryRepository
import net.yuuzu.spenderman.data.source.CategoryDataSource
import net.yuuzu.spenderman.data.source.ExpenseDataSource

class CategoryRepositoryImpl(
    private val categoryDataSource: CategoryDataSource,
    private val expenseDataSource: ExpenseDataSource
) : CategoryRepository {
    
    override suspend fun getAll(): Flow<List<Category>> {
        return categoryDataSource.getAll()
    }
    
    override suspend fun getById(id: String): Flow<Category?> {
        return categoryDataSource.getById(id)
    }
    
    override suspend fun add(item: Category): Boolean {
        return categoryDataSource.add(item)
    }
    
    override suspend fun update(item: Category): Boolean {
        return categoryDataSource.update(item)
    }
    
    override suspend fun delete(id: String): Boolean {
        return categoryDataSource.delete(id)
    }
    
    override suspend fun deleteAll(): Boolean {
        return categoryDataSource.deleteAll()
    }
    
    override suspend fun getIncomeCategories(): Flow<List<Category>> {
        return categoryDataSource.getIncomeCategories()
    }
    
    override suspend fun getExpenseCategories(): Flow<List<Category>> {
        return categoryDataSource.getExpenseCategories()
    }
    
    override suspend fun getCategoryWithMostExpenses(): Flow<Category?> {
        return expenseDataSource.getOutcomeExpenses().map { expenses ->
            val categoryCounts = expenses.groupBy { it.category }
                .mapValues { it.value.size }
            
            if (categoryCounts.isEmpty()) {
                null
            } else {
                val mostUsedCategoryId = categoryCounts.maxByOrNull { it.value }?.key
                mostUsedCategoryId?.let { id ->
                    categoryDataSource.getById(id).firstOrNull()
                }
            }
        }
    }
    
    override suspend fun getCategoryWithMostIncome(): Flow<Category?> {
        return expenseDataSource.getIncomeExpenses().map { expenses ->
            val categoryCounts = expenses.groupBy { it.category }
                .mapValues { it.value.size }
            
            if (categoryCounts.isEmpty()) {
                null
            } else {
                val mostUsedCategoryId = categoryCounts.maxByOrNull { it.value }?.key
                mostUsedCategoryId?.let { id ->
                    categoryDataSource.getById(id).firstOrNull()
                }
            }
        }
    }
}
