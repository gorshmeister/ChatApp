package ru.gorshenev.themesstyles.presentation.ui.profile.rx.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileAction
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileInternalAction
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileState
import javax.inject.Inject

class LoadProfileMiddleware @Inject constructor(private val repository: ProfileRepository) :
    Middleware<ProfileAction, ProfileState> {

    override fun bind(
        actions: Observable<ProfileAction>,
        state: Observable<ProfileState>
    ): Observable<ProfileAction> {
        return actions.ofType(ProfileAction.LoadProfile::class.java)
            .flatMapSingle<ProfileAction> {
                repository.getUser()
                    .map<ProfileInternalAction> { result ->
                        ProfileInternalAction.LoadResult(
                            result.members.firstName,
                            result.members.avatarUrl
                        )
                    }
                    .onErrorReturn { ProfileInternalAction.LoadError(it) }
            }
            .startWith(ProfileInternalAction.StartLoading)
    }
}
