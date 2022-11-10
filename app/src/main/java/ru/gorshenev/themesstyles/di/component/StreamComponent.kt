package ru.gorshenev.themesstyles.di.component

import dagger.Subcomponent
import ru.gorshenev.themesstyles.di.module.StreamModule
import ru.gorshenev.themesstyles.di.scope.StreamScope
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Subcomponent(modules = [StreamModule::class])
@StreamScope
interface StreamComponent {
    fun inject(fragment: StreamFragment)

    @Subcomponent.Builder
    interface Builder {

        fun build(): StreamComponent
    }

}