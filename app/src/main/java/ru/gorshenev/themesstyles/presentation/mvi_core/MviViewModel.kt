package ru.gorshenev.themesstyles.presentation.mvi_core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

class MviViewModel<A : BaseAction, S : BaseState, E : UiEffects>(private val store: Store<A, S, E>) :
    ViewModel() {
    private val wiring = store.wire()

    private var stateBinding: Disposable? = null

    val state
        get() = store.currentState

    fun accept(action: A) {
        store.accept(action)
    }

    override fun onCleared() {
        wiring.dispose()
    }

    fun bind(view: MviView<S, E>) {
        stateBinding = store.bind(view)
    }

    fun unbind() = stateBinding?.dispose()
}