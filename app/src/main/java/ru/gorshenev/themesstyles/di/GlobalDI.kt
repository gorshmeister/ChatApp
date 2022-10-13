package ru.gorshenev.themesstyles.di

import android.content.Context
import android.provider.ContactsContract.Profile
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.data.repositories.people.PeopleRepository
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.presentation.ResourceProvider
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamPresenter
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatPresenter
import ru.gorshenev.themesstyles.presentation.ui.people.PeoplePresenter
import ru.gorshenev.themesstyles.presentation.ui.profile.ProfilePresenter

class GlobalDI private constructor(private val applicationContext: Context) {

    private val db = AppDataBase.getDataBase(applicationContext)

    private val api = Network.api

    val resourceProvider by lazy { ResourceProvider(applicationContext) }


    private val streamRepository by lazy { StreamRepository(db.streamDao(), api) }

    private val chatRepository by lazy { ChatRepository(db.messageDao(), api) }

    private val peopleRepository by lazy { PeopleRepository(api) }

    private val profileRepository by lazy { ProfileRepository(api) }


    val streamPresenter by lazy { StreamPresenter(streamRepository) }

    val chatPresenter by lazy { ChatPresenter(chatRepository) }

    val peoplePresenter by lazy { PeoplePresenter(peopleRepository) }

    val profilePresenter by lazy { ProfilePresenter(profileRepository) }


    companion object {
        lateinit var INSTANSE: GlobalDI

        fun init(applicationContext: Context) {
            INSTANSE = GlobalDI(applicationContext)
        }
    }
}