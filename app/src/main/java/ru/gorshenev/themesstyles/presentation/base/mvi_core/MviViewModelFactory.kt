package ru.gorshenev.themesstyles.presentation.base.mvi_core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MviViewModelFactory<A : BaseAction, S : BaseState, E : BaseEffect>(private val store: Store<A, S, E>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Store::class.java).newInstance(store)
    }
}