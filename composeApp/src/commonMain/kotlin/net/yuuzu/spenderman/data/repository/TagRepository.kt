package net.yuuzu.spenderman.data.repository

import kotlinx.coroutines.flow.Flow
import net.yuuzu.spenderman.data.model.Tag

interface TagRepository : Repository<Tag> {
    suspend fun getTagsByExpenseId(expenseId: String): Flow<List<Tag>>
    suspend fun getMostUsedTags(limit: Int = 10): Flow<List<Tag>>
}
