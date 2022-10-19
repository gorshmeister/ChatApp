package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.repositories.people.PeopleMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.people.PeopleRepository
import ru.gorshenev.themesstyles.presentation.base.presenter.RxPresenter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit

class PeoplePresenter(private val repository: PeopleRepository) :
    RxPresenter<PeopleView>() {

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private var cachedItems: List<ViewTyped> = listOf()


    fun loadPeople() {
        repository.getUsers()
            .map { it.toUi() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showLoading() }
            .doAfterSuccess { view?.stopLoading() }
            .subscribe(
                { people ->
                    view?.showItems(people)
                    cachedItems = people
                },
                { error -> view?.showError(error) },
            ).disposeOnFinish()
    }

    fun searchPeople(query: String) {
        searchSubject.onNext(query)
    }

    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { searchTxt -> initUserSearch(cachedItems, searchTxt) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredPeople -> view?.showItems(filteredPeople) },
                { error -> view?.showError(error) }
            ).disposeOnFinish()
    }

    private fun initUserSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        val peopleUiList = cachedItems.filterIsInstance<PeopleUi>()

        return Single.fromCallable {
            peopleUiList.filter { people ->
                val nameContainsSearchText = people.name.contains(searchText, true)
                val emailContainsSearchText = people.email.contains(searchText, true)

                when (true) {
                    searchText.isNotEmpty() -> nameContainsSearchText || emailContainsSearchText
                    else -> true
                }
            }
        }
    }

}