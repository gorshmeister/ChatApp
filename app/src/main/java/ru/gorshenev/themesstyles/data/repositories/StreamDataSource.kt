package ru.gorshenev.themesstyles.data.repositories

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.network.model.Stream
import ru.gorshenev.themesstyles.data.network.model.Topic
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import kotlin.random.Random

object StreamDataSource {

//    fun getStreams(count: Int): Observable<List<StreamUi>> =
//        Observable.fromCallable { createStreams(count) }
//            .delay(2, TimeUnit.SECONDS)


    fun createStreamUi(streams: List<Stream>): Observable<List<StreamUi>> {
        val list = mutableListOf<StreamUi>()
        streams.map { stream ->
            list += StreamUi(
                id = stream.streamId,
                name = stream.name,
                topics = emptyList()
            )
        }
        return Observable.fromCallable { list.toList() }
    }

    fun createTopicUi(topics: List<Topic>): Observable<List<TopicUi>> {
        val list = mutableListOf<TopicUi>()
        topics.map { topic ->
            list += TopicUi(
                id = topic.maxId,
                name = topic.name
            )
        }
        return Observable.fromCallable { list.toList() }
    }

    private fun createStreams(count: Int): List<StreamUi> {
        var id = 0
        var cnt = 1
        return List(count) {

            val n = Random.nextInt(streamNames.size - 1)
            val m = Random.nextInt(topicNames.size - 1)
            StreamUi(
                id = ++id,
                name = "#${streamNames[n]} №${cnt}",
                topics = listOf(
                    TopicUi(++id, "${topicNames[m]} №$cnt"),
                    TopicUi(++id, "${topicNames[m + 1]} №${++cnt}"),
                )
            )
        }
    }

    private val streamNames = listOf(
        "Officer",
        "Volunteer",
        "Recruits",
        "Humans",
        "Heroes And",
        "Strangers And",
        "Revenge",
        "Scourge",
        "Experience In",
        "Understanding",
        "Droid",
        "Creature",
        "Doctors",
        "Rebels",
        "Defenders And",
        "Figures And",
        "Hatred",
        "Construction",
        "Mother Of",
        "Stranger To"
    )

    private val topicNames = listOf(
        "Hero",
        "Friend",
        "Leaders",
        "Enemies",
        "Agents And",
        "Mercenaries And",
        "Hope",
        "Inception",
        "Experience Of",
        "Disguised In",
        "Horses",
        "Followers",
        "Cats",
        "Politicians",
        "Insects And",
        "Spiders And",
        "Beaches",
        "Working",
        "Lasting",
        "Elegance Of"
    )

}