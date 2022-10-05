package ru.gorshenev.themesstyles.presentation.ui.channels

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.ChatApp
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.StreamRepository
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import java.util.concurrent.TimeUnit

class StreamPresenter(private val view: StreamView) {

    private val db: AppDataBase by lazy { AppDataBase.getDataBase(ChatApp.appContext) }

    private val api: ZulipApi = Network.api

    private val streamRepository: StreamRepository by lazy { StreamRepository(db.streamDao(), api) }

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private val compositeDisposable = CompositeDisposable()

    private var firstLoadedItems: List<ViewTyped> = listOf()

    private var displayedItems: List<ViewTyped> = listOf()


    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { text -> initStreamSearch(firstLoadedItems, text) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredItems ->
                    view.showItems(filteredItems)
                    displayedItems = filteredItems
                },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }
    }

    fun loadStreams(streamType: StreamFragment.StreamType) {
        streamRepository.getStreams(streamType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streams ->
                    firstLoadedItems = streams
                    displayedItems = streams
                    view.showItems(streams)
                    Log.d("database", "=====  Streams Loaded   ==== ")
                },
                { error ->
                    view.stopLoading()
                    view.showError(error)
                },
                { view.stopLoading() }
            ).apply { compositeDisposable.add(this) }
    }

    fun onStreamClick(streamId: Int) {
        Single.just(streamId)
            .flatMap { id -> expandableStream(displayedItems, id) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { expandedList ->
                    displayedItems = expandedList
                    view.showItems(expandedList)
                },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }
    }

    fun onTopicClick(topicId: Int) {
        val topic = displayedItems.find { (it is TopicUi) && it.id == topicId } as? TopicUi
        val stream =
            displayedItems.find { (it is StreamUi) && it.topics.contains(topic) } as? StreamUi
        if (topic != null && stream != null) {
            view.goToChat(topic, stream)
        }
    }

    fun searchStream(query: String) {
        searchSubject.onNext(query)
    }

    fun onClear() {
        compositeDisposable.clear()
    }

    private fun expandableStream(
        items: List<ViewTyped>,
        targetStreamId: Int
    ): Single<List<ViewTyped>> {
        val toDeleteIds = mutableListOf<Int>()
        return Single.fromCallable {
            items.flatMap { item ->
                when (item) {
                    is StreamUi -> when {
                        item.id == targetStreamId && !item.isExpanded -> {
                            listOf(item.copy(isExpanded = true)) + item.topics
                        }
                        item.id == targetStreamId && item.isExpanded -> {
                            toDeleteIds.addAll(item.topics.map { it.id })
                            listOf(item.copy(isExpanded = false))
                        }
                        else -> listOf(item)
                    }
                    is TopicUi -> when (item.id) {
                        in toDeleteIds -> {
                            toDeleteIds - item.id
                            listOf()
                        }
                        else -> listOf(item)
                    }

                    else -> listOf(item)
                }
            }
        }
    }

    private fun initStreamSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        val digits = searchText.filter { it.isDigit() }

        return Single.fromCallable {
            when {
                digits.isNotEmpty() -> {
                    val text = searchText.filter { !it.isDigit() }

                    cachedItems.filter { item ->
                        item is StreamUi &&
                                (item.name.contains(text, true) && item.name.contains(
                                    digits,
                                    true
                                ) ||
                                        (item.topics.any {
                                            it.name.contains(text, true) && it.name.contains(
                                                digits,
                                                true
                                            )
                                        }))
                    }
                }
                searchText.isNotEmpty() -> {
                    cachedItems.filter { item ->
                        item is StreamUi && (item.name.contains(searchText, true) ||
                                item.topics.any { it.name.contains(searchText, true) })
                    }
                }
                else -> cachedItems
            }
        }
    }


//    fun loadStreamsFromDataBase(streamType: StreamFragment.StreamType) {
//        view.repository().getStreams(streamType)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doAfterSuccess {
//                Log.d("database", "=====  Streams Loaded From DATABASE end.  ==== ")
//                view.repository().deleteStreamsAndTopics(streamType)
//                Log.d("database", "streams delete from DB")
//            }
//            .subscribe(
//                { streams ->
//                    firstLoadedItems = streams
//                    displayedItems = streams
//                    view.showItems(streams)
//                    if (streams.isNotEmpty()) view.stopLoading()
//                    Log.d("database", "=====  Streams Loaded From DATABASE  ==== ")
//
//                },
//                { error -> view.showError(error) },
//            ).apply { compositeDisposable.add(this) }
//    }
//
//    fun loadStreams(streamType: StreamFragment.StreamType) {
//        when (streamType) {
//            StreamFragment.StreamType.SUBSCRIBED -> api.getStreamsSubs()
//            StreamFragment.StreamType.ALL_STREAMS -> api.getStreamsAll()
//        }
//            .flatMap { response ->
//                Observable.fromIterable(response.streams)
//                    .flatMapSingle { str ->
//                        Single.zip(
//                            Single.just(str),
//                            api.getTopics(str.streamId),
//                            ::createStreamUi,
//                            )
//                    }.toList()
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doAfterSuccess { view.stopLoading() }
//            .subscribe(
//                { streams ->
//                    firstLoadedItems = streams
//                    displayedItems = streams
//                    view.showItems(streams)
//                    Log.d("database", "=====  Streams Loaded From Network  ==== ")
//                    Log.d("database", "=====  Streams Loaded From Network end...  ==== ")
//                    Log.d("database", "                         ----")
//                },
//                { error ->
//                    view.stopLoading()
//                    view.showError(error)
//                }).apply { compositeDisposable.add(this) }
//    }

}
