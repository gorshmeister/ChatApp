package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.people.PeopleMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.people.PeopleRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware

class PeopleUploadMiddleware(private val repository: PeopleRepository) :
    Middleware<PeopleAction, PeopleState> {
    override fun bind(
        actions: Observable<PeopleAction>,
        state: Observable<PeopleState>
    ): Observable<PeopleAction> {
        return actions.ofType(PeopleAction.UploadUsers::class.java)
            .flatMap {
                repository.getUsers()
                    .map<PeopleInternalAction> { PeopleInternalAction.LoadResult(it.toUi()) }
                    .toObservable()
                    .onErrorReturn { PeopleInternalAction.LoadError(it) }
                    .startWith(PeopleInternalAction.StartLoading)
            }
    }
}