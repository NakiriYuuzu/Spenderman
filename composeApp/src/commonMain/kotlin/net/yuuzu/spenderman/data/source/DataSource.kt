package net.yuuzu.spenderman.data.source

import kotlinx.coroutines.flow.Flow

interface DataSource<T> {
    suspend fun getAll(): Flow<List<T>>
    suspend fun getById(id: String): Flow<T?>
    suspend fun add(item: T): Boolean
    suspend fun update(item: T): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun deleteAll(): Boolean
}
