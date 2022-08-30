package ru.gorshenev.themesstyles.fragments.people

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.PeopleDataSource
import ru.gorshenev.themesstyles.Utils
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import java.util.concurrent.TimeUnit

class PeoplePresenter(private val view: PeopleView) {

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMap { searchTxt -> Utils.initUserSearchObservable(cachedItems, searchTxt) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { filteredPeople -> view.showItems(filteredPeople) }
            .apply { compositeDisposable.add(this) }
    }

    fun loadPeople(count: Int) {
        PeopleDataSource.getPeopleObservable(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { view.showLoading() }
            .subscribe(
                { people ->
                    cachedItems = people
                    view.showItems(people)
                },
                { error -> view.showError(error) },
                { view.stopLoading() }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun searchPeople(query: String) {
        searchSubject.onNext(query)
    }

    fun onClear() {
        compositeDisposable.clear()
    }

}