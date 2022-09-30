package ru.gorshenev.themesstyles.data.repositories.stream

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.dao.StreamDao
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.StreamModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

class StreamRepository(private val streamDao: StreamDao) {

    private val api: ZulipApi = Network.api

    fun getStreams(streamType: StreamFragment.StreamType): Flowable<List<StreamModel>> {
        return Single.concatArrayEager(
            getStreamsFromDb(streamType),
            getStreamsFromApi(streamType)
        )
    }

    private fun getStreamsFromDb(streamType: StreamFragment.StreamType): Single<List<StreamModel>> {
        return streamDao.getStreams(streamType)
            .map { it.toDomain() }
            .subscribeOn(Schedulers.io())
    }

    private fun getStreamsFromApi(streamType: StreamFragment.StreamType): Single<List<StreamModel>> {
        return when (streamType) {
            StreamFragment.StreamType.SUBSCRIBED -> api.getStreamsSubs()
            StreamFragment.StreamType.ALL_STREAMS -> api.getStreamsAll()
        }
            .flatMap { response ->
                Observable.fromIterable(response.streams)
                    .flatMapSingle { str ->
                        Single.zip(
                            Single.just(str),
                            api.getTopics(str.streamId),
                        ) { stream, topicsResponse ->
                            stream.toDomain(topicsResponse.topics)
                        }
                    }.toList()
                    .doOnEvent { streams, _ ->
                        replaceAll(streams, streamType)
                    }

            }.subscribeOn(Schedulers.io())
            .doAfterSuccess { Log.d("database", "=====  Streams Loaded From NETWORK  ==== ") }
    }

    private fun replaceAll(streams: List<StreamModel>, streamType: StreamFragment.StreamType) {
        val streamsEntity = streams.toEntity(streamType)
        val topicsEntity = streams.flatMap { it.topics.toEntity(streamType, it.id) }
        streamDao.replaceAll(streamsEntity, topicsEntity, streamType)
    }

}