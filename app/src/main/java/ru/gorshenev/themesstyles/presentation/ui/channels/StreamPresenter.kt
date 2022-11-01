package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.presentation.base.presenter.RxPresenter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import java.util.concurrent.TimeUnit

class StreamPresenter(private val repository: StreamRepository) :
    RxPresenter<StreamView>() {

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private var firstLoadedItems: List<ViewTyped> = listOf()

    private var displayedItems: List<ViewTyped> = listOf()


    fun loadStreams(streamType: StreamFragment.StreamType) {
        repository.getStreams(streamType)
            .map { streamModels -> streamModels.toUi(streamType) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streams ->
                    firstLoadedItems = streams
                    displayedItems = streams
                    view?.showItems(streams)
                    view?.stopLoading()
                },
                { error ->
                    view?.stopLoading()
                    view?.showError(error)
                },
            ).disposeOnFinish()
    }

    fun onStreamClick(streamId: Int) {
        Single.just(streamId)
            .flatMap { id -> expandableStream(displayedItems, id) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { expandedList ->
                    displayedItems = expandedList
                    view?.showItems(expandedList)
                },
                { error -> view?.showError(error) })
            .disposeOnFinish()
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

    fun onTopicClick(topicId: Int) {
//        val topic = displayedItems.find { (it is TopicUi) && it.id == topicId } as? TopicUi
//        val stream =
//            displayedItems.find { (it is StreamUi) && it.topics.contains(topic) } as? StreamUi

        val topic =
            displayedItems.filterIsInstance<TopicUi>().find { it.id == topicId }
        val stream =
            displayedItems.filterIsInstance<StreamUi>()
                .find { it.topics.contains(topic) }

        if (topic != null && stream != null) {
            view?.goToChat(topic, stream)
        }
    }

    fun searchStream(query: String) {
        searchSubject.onNext(query)
    }

    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { text -> initStreamSearch(firstLoadedItems, text) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredItems ->
                    view?.showItems(filteredItems)
                    displayedItems = filteredItems
                },
                { error -> view?.showError(error) })
            .disposeOnFinish()
    }

    private fun initStreamSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        val text = searchText.filter { !it.isDigit() }
        val digits = searchText.filter { it.isDigit() }
        val streamUiList = cachedItems.filterIsInstance<StreamUi>()

        return Single.fromCallable {
            streamUiList.filter { stream ->

                val nameContainsText = stream.name.contains(text, true)
                val nameContainsDigits = stream.name.contains(digits, true)
                val topicContainsTextOrDigit = stream.topics.any {
                    it.name.contains(text, true) && it.name.contains(digits, true)
                }
                val nameContainsSearchText = stream.name.contains(searchText, true)
                val topicContainsSearchText =
                    stream.topics.any { it.name.contains(searchText, true) }

                when (true) {
                    digits.isNotEmpty() -> {
                        nameContainsText && nameContainsDigits || topicContainsTextOrDigit
                    }
                    searchText.isNotEmpty() -> {
                        nameContainsSearchText || topicContainsSearchText
                    }
                    else -> true
                }
            }
        }
    }

}
