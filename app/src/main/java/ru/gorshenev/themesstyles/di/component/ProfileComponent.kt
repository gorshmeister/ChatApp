package ru.gorshenev.themesstyles.di.component

import dagger.Subcomponent
import ru.gorshenev.themesstyles.di.module.ProfileModule
import ru.gorshenev.themesstyles.di.scope.ProfileScope
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileFragment

@Subcomponent(modules = [ProfileModule::class])
@ProfileScope
interface ProfileComponent {
    fun inject(fragment: ProfileFragment)

    @Subcomponent.Builder
    interface Builder {

        fun build(): ProfileComponent
    }

}