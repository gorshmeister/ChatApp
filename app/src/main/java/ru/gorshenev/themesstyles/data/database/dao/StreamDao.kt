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
    fun deleteStreams(strType: StreamFragment.StreamType): Single<Unit>

    @Query("DELETE FROM topic WHERE type in (:type)")
    fun deleteTopics(type: StreamFragment.StreamType): Single<Unit>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stream: StreamEntity): Single<Unit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(topics: List<TopicEntity>): Single<Unit>

}