package ru.gorshenev.themesstyles.di

import android.content.Context
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.data.repositories.people.PeopleRepository
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.mvi.Store
import ru.gorshenev.themesstyles.mvi.UiState
import ru.gorshenev.themesstyles.mvi.mvi_profile.ProfileMiddleware
import ru.gorshenev.themesstyles.mvi.mvi_profile.ProfileReducer

class GlobalDI private constructor(private val applicationContext: Context) {

    private val db = AppDataBase.getDataBase(applicationContext)

    private val api = Network.api

    val streamRepository by lazy { StreamRepository(db.streamDao(), api) }

    val chatRepository by lazy { ChatRepository(db.messageDao(), api) }

    val peopleRepository by lazy { PeopleRepository(api) }

    val profileRepository by lazy { ProfileRepository(api) }


    val profileStore = (Store(
        ProfileReducer(),
        listOf(ProfileMiddleware(profileRepository)),
        UiState(loading = true, data = null, error = null)
    ))

    companion object {
        lateinit var INSTANSE: GlobalDI

        fun init(applicationContext: Context) {
            INSTANSE = GlobalDI(applicationContext)
        }
    }
}