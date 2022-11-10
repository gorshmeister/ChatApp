package ru.gorshenev.themesstyles.di.component

import dagger.Subcomponent
import ru.gorshenev.themesstyles.di.module.PeopleModule
import ru.gorshenev.themesstyles.di.scope.PeopleScope
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleFragment

@Subcomponent(modules = [PeopleModule::class])
@PeopleScope
interface PeopleComponent {
    fun inject(fragment: PeopleFragment)

    @Subcomponent.Builder
    interface Builder {

        fun build(): PeopleComponent
    }

}