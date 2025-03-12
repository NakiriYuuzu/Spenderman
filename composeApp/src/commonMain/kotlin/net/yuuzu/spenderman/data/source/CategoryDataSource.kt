package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.yuuzu.spenderman.data.model.Category

class CategoryDataSource(settings: Settings) : 
    SettingsDataSourceImpl<Category>(settings, Category.serializer(), "category") {
    
    override fun getId(item: Category): String = item.id
    
    suspend fun getIncomeCategories(): Flow<List<Category>> {
        return getAll().map { categories ->
            categories.filter { it.isIncome }
        }
    }
    
    suspend fun getExpenseCategories(): Flow<List<Category>> {
        return getAll().map { categories ->
            categories.filter { !it.isIncome }
        }
    }
}
