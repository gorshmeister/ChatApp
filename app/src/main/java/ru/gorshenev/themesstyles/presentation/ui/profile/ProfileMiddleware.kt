package ru.gorshenev.themesstyles.presentation.ui.profile

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.mvi_core.UiEffects

class ProfileMiddleware(private val repository: ProfileRepository) :
    Middleware<ProfileAction, ProfileState> {

    override fun bind(
        actions: Observable<ProfileAction>,
        state: Observable<ProfileState>
    ): Observable<ProfileAction> {

        return actions.ofType(ProfileAction.UploadProfile::class.java)
            .flatMap {
                repository.getUser()
                    .map<ProfileInternalAction> { result ->
                        ProfileInternalAction.ProfileSuccessAction(
                            result.members.firstName,
                            result.members.avatarUrl
                        )
                    }
                    .toObservable()
                    .onErrorReturn { e ->
                        ProfileFragment.uiEffect.accept(UiEffects.SnackBar(e))
                        ProfileInternalAction.ProfileFailureAction(e)
                    }
                    .startWith(ProfileInternalAction.ProfileLoadingAction)
            }
    }
}
