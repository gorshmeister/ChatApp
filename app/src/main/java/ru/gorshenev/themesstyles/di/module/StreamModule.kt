package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.StreamScope
import ru.gorshenev.themesstyles.presentation.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.channels.*
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.StreamOnStreamClickMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.StreamOnTopicClickMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.StreamSearchMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.StreamUploadMiddleware


@Module
class StreamModule {
    //todo
    //binding into set?

    @Provides
    @StreamScope
    fun provideStreamStore(
        reducer: StreamReducer,
        m1: StreamUploadMiddleware,
        m2: StreamSearchMiddleware,
        m3: StreamOnStreamClickMiddleware,
        m4: StreamOnTopicClickMiddleware,
    ): Store<StreamAction, StreamState, StreamEffect> {
        return Store(
            reducer = reducer,
            middlewares = listOf(m1, m2, m3, m4),
            initialState = StreamState.Loading
        )
    }

}

