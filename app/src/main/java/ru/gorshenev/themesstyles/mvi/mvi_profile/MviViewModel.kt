package ru.gorshenev.themesstyles.mvi.mvi_profile

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import ru.gorshenev.themesstyles.mvi.MviView
import ru.gorshenev.themesstyles.mvi.Store

class MviViewModel<A : Any,S : Any>(private val store: Store<A,S>) : ViewModel() {
    private val wiring = store.wire()

    private var viewBinding: Disposable? = null

    override fun onCleared() {
        wiring.dispose()
    }

    fun bind(view: MviView<A, S>) {
        viewBinding = store.bind(view)
    }

    fun unbind() = viewBinding?.dispose()
}