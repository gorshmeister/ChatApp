package ru.gorshenev.themesstyles.presentation

import ru.gorshenev.themesstyles.presentation.presenter.Presenter

class MvpHelper<View, P : Presenter<View>>(
    private val callback: ViewCallback<View, P>
) {

    private lateinit var presenter: Presenter<View>

    fun create() {
        presenter = callback.getPresenter()
        presenter.attachView(callback.getMvpView())
    }

    fun destroy(isFinishing: Boolean) {
        presenter.detachView(isFinishing)
    }
}