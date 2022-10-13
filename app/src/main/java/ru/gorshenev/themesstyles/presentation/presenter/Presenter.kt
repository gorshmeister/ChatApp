package ru.gorshenev.themesstyles.presentation.presenter

interface Presenter<View> {

    fun attachView(view: View)

    fun detachView(isFinishing: Boolean)
}