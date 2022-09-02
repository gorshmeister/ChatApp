package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.data.repositories.PeopleDataSource
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit

class PeoplePresenter(private val view: PeopleView) {

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { searchTxt -> initUserSearch(cachedItems, searchTxt) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredPeople -> view.showItems(filteredPeople) },
                { error -> view.showError(error) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun loadPeople(count: Int) {
        PeopleDataSource.getPeople(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.showLoading() }
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

    private fun initUserSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        return Single.fromCallable {
            return@fromCallable when {
                searchText.isEmpty() -> cachedItems
                else -> cachedItems.filter { item ->
                    item is PeopleUi && (item.name.contains(searchText, true) || item.email.contains(searchText, true))
                }
            }
        }
    }

}