package ru.gorshenev.themesstyles.presentation

import ru.gorshenev.themesstyles.presentation.presenter.Presenter

interface ViewCallback<View, P: Presenter<View>> {

    fun getPresenter(): P

    fun getMvpView(): View
}