package ru.gorshenev.themesstyles.di.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.gorshenev.themesstyles.di.module.AppModule
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfileFragment
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun streamComponent(): StreamComponent.Builder
    fun chatComponent(): ChatComponent.Builder
    fun peopleComponent(): PeopleComponent.Builder
    fun profileComponent(): ProfileComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }



}