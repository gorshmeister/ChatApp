package ru.gorshenev.themesstyles.data.repositories.stream

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.channels.StreamModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StreamRepository @Inject constructor(
    private val db: AppDataBase,
    private val api: ZulipApi,
    private val executionScheduler: Scheduler
) {

    fun getStreams(streamType: StreamFragment.StreamType): Observable<List<StreamModel>> {
        return Observable.mergeArrayDelayError(
            getStreamsLocal(streamType),
            getStreamsRemote(streamType)
        )
            .debounce(400, TimeUnit.MILLISECONDS)
            .subscribeOn(executionScheduler)
    }


    private fun getStreamsLocal(streamType: StreamFragment.StreamType): Observable<List<StreamModel>> {
        return db.streamDao().getStreams(streamType)
            .map { streamWithTopics -> streamWithTopics.toDomain() }
            .onErrorReturn { emptyList() }
            .toObservable()
            .subscribeOn(executionScheduler)
    }

    private fun getStreamsRemote(streamType: StreamFragment.StreamType): Observable<MutableList<StreamModel>> {
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
            .onErrorReturn { emptyList<StreamModel>() }
            .toObservable()
            .subscribeOn(executionScheduler)
    }

    private fun replaceAllData(
        streamModels: List<StreamModel>,
        streamType: StreamFragment.StreamType
    ) {
        val streamEntities = streamModels.toEntity(streamType)
        val topicEntities = streamModels.flatMap { it.topics.toEntity(streamType, it.id) }
        db.streamDao().replaceAll(streamEntities, topicEntities, streamType)
    }
}