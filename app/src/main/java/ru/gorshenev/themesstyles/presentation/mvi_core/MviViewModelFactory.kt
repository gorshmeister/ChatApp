package ru.gorshenev.themesstyles.presentation.mvi_core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MviViewModelFactory<A : BaseAction, S : BaseState, E : UiEffects>(private val store: Store<A, S, E>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Store::class.java).newInstance(store)
    }
}