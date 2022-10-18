package ru.gorshenev.themesstyles.data.repositories.stream

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.dao.StreamDao
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.channels.StreamModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import java.util.concurrent.TimeUnit

class StreamRepository(
    private val streamDao: StreamDao,
    private val api: ZulipApi,
    private val executionScheduler: Scheduler = Schedulers.io()
) {

    fun getStreams(streamType: StreamFragment.StreamType): Flowable<List<StreamModel>> {
        return Single.concatArrayEager(
            getStreamsFromDb(streamType),
            getStreamsFromApi(streamType)
        )
            .debounce(400, TimeUnit.MILLISECONDS)
            .materialize()
            .filter { !it.isOnError }
            .dematerialize { streamModels -> streamModels }
            .subscribeOn(executionScheduler)
    }

    private fun getStreamsFromDb(streamType: StreamFragment.StreamType): Single<List<StreamModel>> {
        return streamDao.getStreams(streamType)
            .map { streamWithTopics -> streamWithTopics.toDomain() }
            .subscribeOn(executionScheduler)
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
                        ) { stream, topicResponse ->
                            stream.toDomain(topicResponse.topics, streamType)
                        }
                    }.toList()
            }
            .doOnSuccess { replaceAllData(it, streamType) }
            .subscribeOn(executionScheduler)
    }

    private fun replaceAllData(
        streamModels: List<StreamModel>,
        streamType: StreamFragment.StreamType
    ) {
        val streamEntities = streamModels.toEntity(streamType)
        val topicEntities = streamModels.flatMap { it.topics.toEntity(streamType, it.id) }
        streamDao.replaceAll(streamEntities, topicEntities, streamType)
    }
}