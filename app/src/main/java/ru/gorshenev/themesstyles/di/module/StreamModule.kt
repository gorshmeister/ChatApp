package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.di.scope.StreamScope
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamEffect
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamReducer
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamState
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.ExpandStreamMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.LoadStreamMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.OpenChatMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.SearchStreamMiddleware


@Module
class StreamModule {
    @Provides
    @StreamScope
    fun provideStreamStore(
        reducer: StreamReducer,
        middlewares: List<@JvmSuppressWildcards Middleware<StreamAction, StreamState>>
    ): Store<StreamAction, StreamState, StreamEffect> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = StreamState.Loading
        )
    }

    @Provides
    @StreamScope
    fun provideStreamMiddlewares(repository: StreamRepository): List<Middleware<StreamAction, StreamState>> {
        return listOf(
            LoadStreamMiddleware(repository),
            SearchStreamMiddleware(),
            ExpandStreamMiddleware(),
            OpenChatMiddleware(),
        )
    }

}

