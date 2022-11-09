package ru.gorshenev.themesstyles.presentation.ui.people.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleAction
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleInternalAction
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleState
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchMiddleware @Inject constructor() : Middleware<PeopleAction, PeopleState> {
    override fun bind(
        actions: Observable<PeopleAction>,
        state: Observable<PeopleState>
    ): Observable<PeopleAction> {
        return actions.ofType(PeopleAction.SearchUsers::class.java)
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMap { initUserSearch(it.items, it.query) }
            .map<PeopleAction> { PeopleInternalAction.SearchResult(it) }
            .onErrorReturn { PeopleInternalAction.LoadError(it) }
    }


    private fun initUserSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Observable<List<ViewTyped>> {
        return Observable.fromCallable {
            cachedItems.filterIsInstance<PeopleUi>().filter { people ->
                val nameContainsSearchText = people.name.contains(searchText, true)
                val emailContainsSearchText = people.email.contains(searchText, true)
                nameContainsSearchText || emailContainsSearchText
            }
        }
    }
}

