package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.ProfileScope
import ru.gorshenev.themesstyles.presentation.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileAction
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileEffect
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileReducer
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileState
import ru.gorshenev.themesstyles.presentation.ui.profile.middleware.ProfileMiddleware

@Module
class ProfileModule {
    @Provides
    @ProfileScope
    fun provideProfileStore(
        reducer: ProfileReducer,
        middleware: ProfileMiddleware,
    ): Store<ProfileAction, ProfileState, ProfileEffect> {
        return Store(
            reducer = reducer,
            middlewares = listOf(middleware),
            initialState = ProfileState.Loading
        )
    }
}
