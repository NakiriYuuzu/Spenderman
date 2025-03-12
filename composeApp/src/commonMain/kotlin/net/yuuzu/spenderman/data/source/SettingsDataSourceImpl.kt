package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

abstract class SettingsDataSourceImpl<T>(
    protected val settings: Settings,
    private val serializer: KSerializer<T>,
    private val keyPrefix: String
) : DataSource<T> {
    
    private val json = Json { ignoreUnknownKeys = true }
    private val KEY_ALL_IDS = "${keyPrefix}_all_ids"
    
    abstract fun getId(item: T): String
    
    override suspend fun getAll(): Flow<List<T>> = flow {
        val allIds = getAllIds()
        val items = allIds.mapNotNull { id -> getItemById(id) }
        emit(items)
    }
    
    override suspend fun getById(id: String): Flow<T?> = flow {
        emit(getItemById(id))
    }
    
    override suspend fun add(item: T): Boolean {
        return try {
            val id = getId(item)
            val allIds = getAllIds().toMutableSet()
            allIds.add(id)
            saveAllIds(allIds)
            saveItem(id, item)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun update(item: T): Boolean {
        return try {
            val id = getId(item)
            val allIds = getAllIds()
            if (id in allIds) {
                saveItem(id, item)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun delete(id: String): Boolean {
        return try {
            val allIds = getAllIds().toMutableSet()
            if (allIds.remove(id)) {
                saveAllIds(allIds)
                settings.remove(getItemKey(id))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun deleteAll(): Boolean {
        return try {
            val allIds = getAllIds()
            allIds.forEach { id ->
                settings.remove(getItemKey(id))
            }
            saveAllIds(emptySet())
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun getAllIds(): Set<String> {
        val idsJson = settings.getStringOrNull(KEY_ALL_IDS) ?: return emptySet()
        return json.decodeFromString<Set<String>>(idsJson)
    }
    
    private fun saveAllIds(ids: Set<String>) {

        val idsJson = json.encodeToString(SetSerializer(String.serializer()), ids)
        settings[KEY_ALL_IDS] = idsJson
    }
    
    private fun getItemById(id: String): T? {
        val itemJson = settings.getStringOrNull(getItemKey(id)) ?: return null
        return json.decodeFromString(serializer, itemJson)
    }
    
    private fun saveItem(id: String, item: T) {
        val itemJson = json.encodeToString(serializer, item)
        settings[getItemKey(id)] = itemJson
    }
    
    private fun getItemKey(id: String): String = "${keyPrefix}_$id"
}
