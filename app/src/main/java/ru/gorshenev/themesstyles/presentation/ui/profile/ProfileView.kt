package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.data.network.model.UserResponse
import ru.gorshenev.themesstyles.presentation.ui.BaseView

interface ProfileView : BaseView {

//    fun showLoading()

//    fun stopLoading()
//
//    fun showError(error: Throwable?)

    fun setProfile(userResponse: UserResponse)

    fun showEmptyState()

}
