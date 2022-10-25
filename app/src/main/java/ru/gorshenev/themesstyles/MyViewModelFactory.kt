package ru.gorshenev.themesstyles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.gorshenev.themesstyles.mvi.Store

class MyViewModelFactory<A : Any, S : Any>(private val store: Store<A, S>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Store::class.java).newInstance(store)
    }
}