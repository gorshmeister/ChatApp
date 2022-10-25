package ru.gorshenev.themesstyles.mvi.mvi_profile

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.mvi.Action
import ru.gorshenev.themesstyles.mvi.InternalAction
import ru.gorshenev.themesstyles.mvi.Middleware
import ru.gorshenev.themesstyles.mvi.UiState

class ProfileMiddleware(private val repository: ProfileRepository) :
    Middleware<Action, UiState> {
    override fun bind(
        actions: Observable<Action>,
        state: Observable<UiState>
    ): Observable<Action> {
        return actions.ofType(Action.UploadProfile::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .flatMap { (action, state) ->
                repository.getUser()
                    .map<InternalAction> { result ->
                        InternalAction.ProfileSuccessAction(
                            result.members.firstName,
                            result.members.avatarUrl
                        )
                    }
                    .onErrorReturn { e -> InternalAction.ProfileFailureAction(e) }
                    .toObservable()
                    .startWith { InternalAction.ProfileLoadingAction }
            }
    }
}
