package ru.gorshenev.themesstyles.data.repositories.stream

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.dao.StreamDao
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.channels.StreamModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

class StreamRepository(private val streamDao: StreamDao, private val api: ZulipApi) {

    fun getStreamsFromDb(streamType: StreamFragment.StreamType): Single<List<StreamModel>> {
        return streamDao.getStreams(streamType)
            .map { streamWithTopics -> streamWithTopics.toDomain() }
            .subscribeOn(Schedulers.io())
    }

    fun getStreamsFromApi(streamType: StreamFragment.StreamType): Single<List<StreamModel>> {
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
                            stream.toDomain(topicResponse.topics)
                        }
                    }.toList()
            }
    }

    fun replaceDataInDb(
        streamModels: List<StreamModel>,
        streamType: StreamFragment.StreamType
    ): Completable {
        val streamEntities = streamModels.toEntity(streamType)
        val topicEntities = streamModels.flatMap { it.topics.toEntity(streamType, it.id) }
        return Completable.fromCallable {
            streamDao.replaceAll(streamEntities, topicEntities, streamType)
        }
    }
}