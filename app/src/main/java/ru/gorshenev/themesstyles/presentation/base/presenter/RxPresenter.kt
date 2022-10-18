package ru.gorshenev.themesstyles.presentation.base.presenter

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxPresenter<V> protected constructor() :
    BasePresenter<V>() {
    private val disposables = CompositeDisposable()

    override fun detachView(isFinishing: Boolean) {
        if (isFinishing) {
            disposables.dispose()
        }
        super.detachView(isFinishing)
    }

    protected fun removeDisposables(disposable: Disposable?) {
        disposable?.let { disposables.remove(it) }
    }

    protected fun Disposable.disposeOnFinish(): Disposable {
        disposables.add(this)
        return this
    }

    protected fun dispose(disposable: Disposable) {
        if (!disposables.remove(disposable)) {
            disposable.dispose()
        }
    }

}