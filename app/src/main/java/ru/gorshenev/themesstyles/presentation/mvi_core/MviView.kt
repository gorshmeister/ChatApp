package ru.gorshenev.themesstyles.presentation.mvi_core

import io.reactivex.Observable

interface MviView<A, S, E> {
    val actions: Observable<A>
    val effects: Observable<E>
    fun render(state: S)
    fun handleUiEffects(effect: E)
}