package ru.gorshenev.themesstyles

import android.app.Application
import ru.gorshenev.themesstyles.di.component.AppComponent
import ru.gorshenev.themesstyles.di.component.DaggerAppComponent
import ru.gorshenev.themesstyles.di.component.ProfileComponent

class ChatApp : Application() {

    lateinit var appComponent: AppComponent

    var profileComponent: ProfileComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().context(this).build()
        profileComponent = appComponent.profileComponent().build()
    }

    fun releaseProfile() {
        if (profileComponent != null) {
            profileComponent = null
        }
    }

}
