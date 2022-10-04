package ru.gorshenev.themesstyles

import android.app.Application
import android.content.Context
import ru.gorshenev.themesstyles.data.database.AppDataBase

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        AppDataBase.getDataBase(appContext)
    }

    companion object {
        lateinit var appContext: Context
    }

}