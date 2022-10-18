package ru.gorshenev.themesstyles.presentation.base

import ru.gorshenev.themesstyles.presentation.base.presenter.Presenter

interface ViewCallback<View, P: Presenter<View>> {

    fun getPresenter(): P

    fun getMvpView(): View
}