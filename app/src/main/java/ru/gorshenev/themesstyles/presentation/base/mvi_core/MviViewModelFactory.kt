package ru.gorshenev.themesstyles.presentation.base.mvi_core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject


class MviViewModelFactory<A : BaseAction, S : BaseState, E : BaseEffect> @Inject constructor(
    val store: Store<A, S, E>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Store::class.java).newInstance(store)
    }
}