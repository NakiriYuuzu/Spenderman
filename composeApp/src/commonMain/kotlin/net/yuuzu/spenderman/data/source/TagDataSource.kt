package net.yuuzu.spenderman.data.source

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.yuuzu.spenderman.data.model.Tag

class TagDataSource(settings: Settings) : 
    SettingsDataSourceImpl<Tag>(settings, Tag.serializer(), "tag") {
    
    override fun getId(item: Tag): String = item.id
}
