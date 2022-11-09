package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.PeopleScope
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleAction
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleEffect
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleReducer
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleState
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.people.middleware.PeopleSearchMiddleware
import ru.gorshenev.themesstyles.presentation.ui.people.middleware.PeopleUploadMiddleware

@Module
class PeopleModule {
    @Provides
    @PeopleScope
    fun providePeopleStore(
        reducer: PeopleReducer,
        m1: PeopleUploadMiddleware,
        m2: PeopleSearchMiddleware,
    ): Store<PeopleAction, PeopleState, PeopleEffect> {
        return Store(
            reducer = reducer,
            middlewares = listOf(m1, m2),
            initialState = PeopleState.Loading
        )
    }

    @Provides
    @PeopleScope
    fun providePeopleAdapter(
        holderFactory: PeopleHolderFactory
    ): Adapter<ViewTyped> {
        return Adapter(holderFactory)
    }

}
