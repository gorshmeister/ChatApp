package ru.gorshenev.themesstyles

import android.app.Application
import android.content.Context
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.di.GlobalDI

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        AppDataBase.getDataBase(appContext)
        GlobalDI.init(appContext)
    }

    companion object {
        lateinit var appContext: Context
    }

}