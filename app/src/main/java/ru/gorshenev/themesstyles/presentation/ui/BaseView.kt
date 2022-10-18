package ru.gorshenev.themesstyles.presentation.ui

interface BaseView {
    fun showError(error: Throwable?)

    fun showLoading()

    fun stopLoading()
}