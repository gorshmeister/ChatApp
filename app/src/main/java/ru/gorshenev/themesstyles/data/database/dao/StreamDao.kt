package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.StreamWithTopicsEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Dao
interface StreamDao {

    @Transaction
    @Query("SELECT * FROM stream WHERE strType LIKE :streamType")
    fun getStreams(streamType: StreamFragment.StreamType): Single<List<StreamWithTopicsEntity>>


    @Query("DELETE FROM stream WHERE strType in (:streamType)")
    fun deleteStreams(streamType: StreamFragment.StreamType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStreams(streams: List<StreamEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTopics(topics: List<TopicEntity>)


    @Transaction
    fun replaceAll(streams: List<StreamEntity>, topics: List<TopicEntity>, strType: StreamFragment.StreamType){
        deleteStreams(strType)
        insertStreams(streams)
        insertTopics(topics)
    }


}