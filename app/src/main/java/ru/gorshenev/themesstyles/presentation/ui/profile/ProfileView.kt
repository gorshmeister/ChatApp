package ru.gorshenev.themesstyles.presentation.ui.profile

import java.lang.Error

interface ProfileView {

    fun showLoading()

    fun stopLoading()

    fun showError(error: Throwable?)

    fun setProfile(name: String, avatar: String)



}
