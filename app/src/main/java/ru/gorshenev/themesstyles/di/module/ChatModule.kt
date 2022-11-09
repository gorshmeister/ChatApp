package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.ChatScope
import ru.gorshenev.themesstyles.presentation.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.chat.*
import ru.gorshenev.themesstyles.presentation.ui.chat.middleware.*

@Module
class ChatModule {
    @Provides
    @ChatScope
    fun provideBottomSheet(): BottomSheet {
        return BottomSheet()
    }

    @Provides
    @ChatScope
    fun provideChatStore(
        reducer: ChatReducer,
        m1: UploadMiddleware,
        m2: OnEmojiClickMiddleware,
        m3: UploadMoreMiddleware,
        m4: SendMessageMiddleware,
        m5: RegisterMessageQueueMiddleware,
        m6: GetQueueMessageMiddleware,
        m7: RegisterReactionQueueMiddleware,
        m8: GetQueueReactionMiddleware
    ): Store<ChatAction, ChatState, ChatEffect> {
        return Store(
            reducer = reducer,
            middlewares = listOf(m1, m2, m3, m4, m5, m6, m7, m8),
            initialState = ChatState.Loading
        )
    }
}
