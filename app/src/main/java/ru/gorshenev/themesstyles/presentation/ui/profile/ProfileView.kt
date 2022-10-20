package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.base.BaseView

interface ProfileView : BaseView {

    fun setProfile(name: String, avatarUrl: String)

    fun showEmptyState()

}
