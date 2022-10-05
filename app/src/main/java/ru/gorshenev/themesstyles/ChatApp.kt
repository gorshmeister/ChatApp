package ru.gorshenev.themesstyles

import android.app.Application
import android.content.Context
import ru.gorshenev.themesstyles.data.database.AppDataBase

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setContext(this)
        val db = AppDataBase.getDataBase(appContext)
    }

    private fun setContext(context: Context) {
        appContext = context
    }

    companion object {
        lateinit var appContext: Context
    }

}