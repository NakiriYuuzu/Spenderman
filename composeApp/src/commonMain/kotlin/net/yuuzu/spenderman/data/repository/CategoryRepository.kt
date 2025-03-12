package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.Category

interface CategoryRepository : Repository<Category> {
    suspend fun getIncomeCategories(): Flow<List<Category>>
    suspend fun getExpenseCategories(): Flow<List<Category>>
    suspend fun getCategoryWithMostExpenses(): Flow<Category?>
    suspend fun getCategoryWithMostIncome(): Flow<Category?>
}
