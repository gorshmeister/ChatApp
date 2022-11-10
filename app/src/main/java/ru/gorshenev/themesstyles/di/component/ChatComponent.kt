package ru.gorshenev.themesstyles.di.component

import dagger.Subcomponent
import ru.gorshenev.themesstyles.di.module.ChatModule
import ru.gorshenev.themesstyles.di.scope.ChatScope
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment

@Subcomponent(modules = [ChatModule::class])
@ChatScope
interface ChatComponent {
    fun inject(fragment: ChatFragment)

    @Subcomponent.Builder
    interface Builder {

        fun build(): ChatComponent
    }

}