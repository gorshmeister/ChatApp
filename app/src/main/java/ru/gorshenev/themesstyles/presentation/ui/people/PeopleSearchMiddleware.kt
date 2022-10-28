package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit

class PeopleSearchMiddleware() : Middleware<PeopleAction, PeopleState> {
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private var cachedItems = emptyList<ViewTyped>()

    override fun bind(
        actions: Observable<PeopleAction>,
        state: Observable<PeopleState>
    ): Observable<PeopleAction> {
        return actions.ofType(PeopleAction.SearchUsers::class.java)
            .map {
                cachedItems = it.items
                searchSubject.onNext(it.query)
            }
            .flatMap {
                searchSubject
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMap { searchText -> initUserSearch(cachedItems, searchText) }
                    .map<PeopleInternalAction> { PeopleInternalAction.LoadResult(it) }
                    .onErrorReturn { PeopleInternalAction.LoadError(it) }
            }
    }

    private fun initUserSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Observable<List<ViewTyped>> {
        val peopleUiList = cachedItems.filterIsInstance<PeopleUi>()

        return Observable.fromCallable {
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