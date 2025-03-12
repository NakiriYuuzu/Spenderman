package net.yuuzu.spenderman.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import net.yuuzu.spenderman.data.model.Tag
import net.yuuzu.spenderman.data.repository.TagRepository
import net.yuuzu.spenderman.data.source.ExpenseDataSource
import net.yuuzu.spenderman.data.source.TagDataSource

class TagRepositoryImpl(
    private val tagDataSource: TagDataSource,
    private val expenseDataSource: ExpenseDataSource
) : TagRepository {
    
    override suspend fun getAll(): Flow<List<Tag>> {
        return tagDataSource.getAll()
    }
    
    override suspend fun getById(id: String): Flow<Tag?> {
        return tagDataSource.getById(id)
    }
    
    override suspend fun add(item: Tag): Boolean {
        return tagDataSource.add(item)
    }
    
    override suspend fun update(item: Tag): Boolean {
        return tagDataSource.update(item)
    }
    
    override suspend fun delete(id: String): Boolean {
        return tagDataSource.delete(id)
    }
    
    override suspend fun deleteAll(): Boolean {
        return tagDataSource.deleteAll()
    }
    
    override suspend fun getTagsByExpenseId(expenseId: String): Flow<List<Tag>> {
        return expenseDataSource.getById(expenseId).map { expense ->
            if (expense == null) {
                emptyList()
            } else {
                val allTags = tagDataSource.getAll().firstOrNull() ?: emptyList()
                allTags.filter { tag -> expense.tags.contains(tag.id) }
            }
        }
    }
    
    override suspend fun getMostUsedTags(limit: Int): Flow<List<Tag>> {
        return expenseDataSource.getAll().map { expenses ->
            val tagCounts = mutableMapOf<String, Int>()
            
            expenses.forEach { expense ->
                expense.tags.forEach { tagId ->
                    tagCounts[tagId] = (tagCounts[tagId] ?: 0) + 1
                }
            }
            
            val topTagIds = tagCounts.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }
            
            val allTags = tagDataSource.getAll().firstOrNull() ?: emptyList()
            allTags.filter { it.id in topTagIds }
                .sortedByDescending { tagCounts[it.id] ?: 0 }
        }
    }
}
