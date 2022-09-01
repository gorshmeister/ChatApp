package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.data.repositories.PeopleDataSource
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import java.util.concurrent.TimeUnit

class PeoplePresenter(private val view: PeopleView) {

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    //todo почему?
    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { searchTxt -> Utils.initUserSearch(cachedItems, searchTxt) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredPeople -> view.showItems(filteredPeople) },
                { error -> view.showError(error) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun loadPeople(count: Int) {
        view.showLoading()
        PeopleDataSource.getPeople(count)
            .subscribeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { people ->
                    view.showItems(people)
                    cachedItems = people
                },
                { error -> view.showError(error) },
                { view.stopLoading() })
            .apply { compositeDisposable.add(this) }
    }

    fun searchPeople(query: String) {
        searchSubject.onNext(query)
    }

    fun onClear() {
        compositeDisposable.clear()
    }

}