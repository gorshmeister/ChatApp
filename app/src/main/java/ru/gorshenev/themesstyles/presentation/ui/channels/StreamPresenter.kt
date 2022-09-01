package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.data.mappers.StreamMapper
import ru.gorshenev.themesstyles.data.repositories.StreamDataSource
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import java.util.concurrent.TimeUnit

class StreamPresenter(private val view: StreamView) {

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()


    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { text -> Utils.initStreamSearch(cachedItems, text) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredItems ->
                    view.showItems(filteredItems) },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }
    }

    fun loadStreams(count: Int) {
        StreamDataSource.getStreams(count)
            .subscribeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streams ->
                    view.showItems(streams)
                    cachedItems = streams
                },
                { error -> view.showError(error) },
                { view.stopLoading() })
            .apply { compositeDisposable.add(this) }
    }

    fun onStreamClick(streamId: Int) {
        Observable.just(streamId)
            .flatMapSingle { id -> StreamMapper.expandableStream(view.adapterItems(), id) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updItems ->
                    view.showItems(updItems)
                },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }

    }

    fun searchStream(query: String) {
        searchSubject.onNext(query)
    }

    fun onClear() {
        compositeDisposable.clear()
    }
}
