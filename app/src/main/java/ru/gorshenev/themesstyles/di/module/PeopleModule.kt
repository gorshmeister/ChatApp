package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.data.repositories.people.PeopleRepository
import ru.gorshenev.themesstyles.di.scope.PeopleScope
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleAction
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleEffect
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleReducer
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleState
import ru.gorshenev.themesstyles.presentation.ui.people.middleware.LoadPeopleMiddleware
import ru.gorshenev.themesstyles.presentation.ui.people.middleware.SearchPeopleMiddleware

@Module
class PeopleModule {
    @Provides
    @PeopleScope
    fun providePeopleStore(
        reducer: PeopleReducer,
        middlewares: List<@JvmSuppressWildcards Middleware<PeopleAction, PeopleState>>
    ): Store<PeopleAction, PeopleState, PeopleEffect> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = PeopleState.Loading
        )
    }

    @Provides
    @PeopleScope
    fun providePeopleMiddlewares(repository: PeopleRepository): List<Middleware<PeopleAction, PeopleState>> {
        return listOf(
            LoadPeopleMiddleware(repository),
            SearchPeopleMiddleware()
        )
    }
}
