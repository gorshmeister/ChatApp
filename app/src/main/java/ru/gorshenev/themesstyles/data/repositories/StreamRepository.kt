package ru.gorshenev.themesstyles.data.repositories

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.dao.StreamDao
import ru.gorshenev.themesstyles.data.database.dao.StreamWithTopics
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.GetTopicResponse
import ru.gorshenev.themesstyles.data.network.model.Stream
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class StreamRepository(private val streamDao: StreamDao) {

    private val api: ZulipApi = Network.api

    fun getStreams(streamType: StreamFragment.StreamType): Flowable<List<StreamUi>> {
        return Single.concatArrayEager(
            getStreamsFromDb(streamType),
            getStreamsFromApi(streamType)
        )
    }

    private fun getStreamsFromDb(streamType: StreamFragment.StreamType): Single<List<StreamUi>> {
        return streamDao.getStreams(streamType)
            .map { streamWithTopics -> createStreamUiFromEntity(streamWithTopics) }
            .subscribeOn(Schedulers.io())
            .doAfterSuccess {
                Log.d("database", "=====  Streams Loaded From DATABASE ==== ")
                deleteStreamsAndTopics(streamType)
            }
    }

    private fun getStreamsFromApi(streamType: StreamFragment.StreamType): Single<MutableList<StreamUi>> {
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
                            ::createStreamUi,
                        )
                    }.toList()
            }.subscribeOn(Schedulers.io())
            .doAfterSuccess { Log.d("database", "=====  Streams Loaded From NETWORK  ==== ") }
    }

    private fun createStreamUi(
        stream: Stream,
        response: GetTopicResponse,
    ): StreamUi {
        addToDatabase(response, stream)

        val topics = response.topics.map { topic ->
            TopicUi(
                id = topic.maxId,
                name = topic.name,
                color = stream.color
            )
        }

        return StreamUi(
            id = stream.streamId,
            name = stream.name,
            topics = topics
        )
    }


    private fun addToDatabase(topicResponse: GetTopicResponse, stream: Stream) {
        val x = Single.concat(
            streamDao.insert(
                StreamEntity(
                    streamId = stream.streamId,
                    name = stream.name,
                    color = stream.color,
                    type = when {
                        stream.color != "#2A9D8F" -> StreamFragment.StreamType.SUBSCRIBED
                        else -> StreamFragment.StreamType.ALL_STREAMS
                    }
                )
            ),
            streamDao.insert(topicResponse.topics.map { topic ->
                TopicEntity(
                    streamId = stream.streamId,
                    maxId = topic.maxId,
                    name = topic.name,
                    color = stream.color,
                    type = when {
                        stream.color != "#2A9D8F" -> StreamFragment.StreamType.SUBSCRIBED
                        else -> StreamFragment.StreamType.ALL_STREAMS
                    }
                )
            }
            ))
            .subscribeOn(Schedulers.io())
            .subscribe({ },
                { Log.d("database", "ERROR ADD TO DB $it") }
            )
    }

    private fun deleteStreamsAndTopics(streamType: StreamFragment.StreamType) {
        val disp = streamDao.deleteStreams(streamType)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Log.d("database", "streams delete from DB") },
                { Log.d("database", "DELETE ERROR $it") })
    }

    private fun createStreamUiFromEntity(
        streamWithTopics: List<StreamWithTopics>,
    ): List<StreamUi> {
        return streamWithTopics.map { stream ->
            val topics = stream.topics
                .map { topicEntity ->
                    TopicUi(
                        id = topicEntity.maxId,
                        name = topicEntity.name,
                        color = topicEntity.color
                    )
                }

            StreamUi(
                id = stream.stream!!.streamId,
                name = stream.stream!!.name,
                topics = topics,
            )
        }
    }

}