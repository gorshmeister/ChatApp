package ru.gorshenev.themesstyles.presentation.base.mvi_core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MviViewModel<A : BaseAction, S : BaseState, E : BaseEffect>
@Inject constructor(private val store: Store<A, S, E>) : ViewModel() {

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