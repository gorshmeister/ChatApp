package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Dao
interface StreamDao {

    @Transaction
    @Query("SELECT * FROM stream WHERE strType LIKE :streamType")
    fun getStreams(streamType: StreamFragment.StreamType): Single<List<StreamWithTopics>>

    @Query("DELETE FROM stream WHERE strType in (:strType)")
    fun deleteStreams(strType: StreamFragment.StreamType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStreams(stream: List<StreamEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTopics(topics: List<TopicEntity>)

    @Transaction
    fun replaceAll(stream: List<StreamEntity>, topics: List<TopicEntity>, strType: StreamFragment.StreamType) {
        deleteStreams(strType)
        insertStreams(stream)
        insertTopics(topics)
    }

}