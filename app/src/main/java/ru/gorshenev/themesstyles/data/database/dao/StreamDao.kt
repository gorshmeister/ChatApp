package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.StreamWithTopics
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Dao
interface StreamDao {

    @Transaction
    @Query("SELECT * FROM stream WHERE strType LIKE :streamType")
    fun getStreams(streamType: StreamFragment.StreamType): Single<List<StreamWithTopics>>



    @Query("DELETE FROM stream WHERE strType in (:strType)")
    fun deleteStreams(strType: StreamFragment.StreamType): Completable

    @Query("DELETE FROM topic WHERE type in (:type)")
    fun deleteTopics(type: StreamFragment.StreamType): Completable


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(stream: StreamEntity): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(topics: List<TopicEntity>): Completable

}