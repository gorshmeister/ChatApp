package ru.gorshenev.themesstyles.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.gorshenev.themesstyles.data.database.dao.MessageDao
import ru.gorshenev.themesstyles.data.database.dao.StreamDao
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity

@Database(
    entities = [
        StreamEntity::class,
        TopicEntity::class,
        MessageEntity::class,
        ReactionEntity::class
    ], version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun streamDao(): StreamDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val DB_NAME = "appDataBase"
        const val MESSAGE = "message"
        const val REACTION = "reaction"
        const val STREAM = "stream"
        const val TOPIC = "topic"
    }

}