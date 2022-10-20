package ru.gorshenev.themesstyles.presentation.base.presenter

import androidx.annotation.CallSuper

abstract class BasePresenter<View> protected constructor() : Presenter<View> {

    private var realView: View? = null

    val view: View?
        get() = realView

    @CallSuper
    override fun attachView(view: View) {
        this.realView = view
    }

    @CallSuper
    override fun detachView(isFinishing: Boolean) {
        realView = null
    }

}