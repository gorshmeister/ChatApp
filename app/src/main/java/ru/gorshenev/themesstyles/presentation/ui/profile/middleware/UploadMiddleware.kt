package ru.gorshenev.themesstyles.presentation.ui.profile.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileAction
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileInternalAction
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileState

class UploadMiddleware(private val repository: ProfileRepository) :
    Middleware<ProfileAction, ProfileState> {

    override fun bind(
        actions: Observable<ProfileAction>,
        state: Observable<ProfileState>
    ): Observable<ProfileAction> {
        return actions.ofType(ProfileAction.UploadProfile::class.java)
            .flatMap {
                repository.getUser()
                    .map<ProfileInternalAction> { result ->
                        ProfileInternalAction.LoadResult(
                            result.members.firstName,
                            result.members.avatarUrl
                        )
                    }
                    .toObservable()
                    .onErrorReturn { ProfileInternalAction.LoadError(it) }
                    .startWith(ProfileInternalAction.StartLoading)
            }
    }
}
