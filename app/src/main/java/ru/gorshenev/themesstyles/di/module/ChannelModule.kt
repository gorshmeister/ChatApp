package ru.gorshenev.themesstyles.di.module

import dagger.Module
import dagger.Provides
import ru.gorshenev.themesstyles.di.scope.ChannelScope
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.view_pager.PagerAdapter

@Module
class ChannelModule {

    @Provides
    @ChannelScope
    fun providePagerAdapter(channelsFragment: ChannelsFragment): PagerAdapter {
        return PagerAdapter(channelsFragment.parentFragmentManager, channelsFragment.lifecycle)
    }
}
