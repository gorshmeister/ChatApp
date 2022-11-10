package ru.gorshenev.themesstyles.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.di.scope.ChatScope
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModelFactory
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.chat.*
import ru.gorshenev.themesstyles.presentation.ui.chat.middleware.*

@Module
class ChatModule {

    @Provides
    @ChatScope
    fun provideChatStore(
        reducer: ChatReducer,
        middlewares: List<@JvmSuppressWildcards Middleware<ChatAction, ChatState>>
    ): Store<ChatAction, ChatState, ChatEffect> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = ChatState.Loading
        )
    }

    @Provides
    @ChatScope
    fun provideChatMiddlewares(
        repository: ChatRepository
    ): List<Middleware<ChatAction, ChatState>> {
        return listOf(
            LoadMessagesMiddleware(repository),
            LoadMoreMessagesMiddleware(repository),
            OnEmojiClickMiddleware(repository),
            SendMessageMiddleware(repository),
            RegisterMessageQueueMiddleware(repository),
            GetQueueMessageMiddleware(repository),
            RegisterReactionQueueMiddleware(repository),
            GetQueueReactionMiddleware(repository)
        )
    }
}
