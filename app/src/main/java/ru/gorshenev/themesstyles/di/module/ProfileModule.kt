package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.ProfileScope
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileAction
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileEffect
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileReducer
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileState
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.middleware.LoadProfileMiddleware

@Module
class ProfileModule {
    @Provides
    @ProfileScope
    fun provideProfileStore(
        reducer: ProfileReducer,
        middleware: LoadProfileMiddleware,
    ): Store<ProfileAction, ProfileState, ProfileEffect> {
        return Store(
            reducer = reducer,
            middlewares = listOf(middleware),
            initialState = ProfileState.Loading
        )
    }
}
