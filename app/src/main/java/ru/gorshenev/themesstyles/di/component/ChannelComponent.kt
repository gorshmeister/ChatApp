package ru.gorshenev.themesstyles.di.component

import dagger.BindsInstance
import dagger.Subcomponent
import ru.gorshenev.themesstyles.di.module.ChannelModule
import ru.gorshenev.themesstyles.di.scope.ChannelScope
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment

@Subcomponent(modules = [ChannelModule::class])
@ChannelScope
interface ChannelComponent {
    fun inject(fragment: ChannelsFragment)

    @dagger.Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun fragment(fragment: ChannelsFragment): Builder

        fun build(): ChannelComponent
    }

}