package ru.gorshenev.themesstyles.presentation.base

interface BaseView {
    fun showError(error: Throwable?)

    fun showLoading()

    fun stopLoading()
}