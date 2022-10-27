package ru.gorshenev.themesstyles.presentation.mvi_core

interface Reducer<A, S, E> {
    fun reduceToState(action: A, state: S): S
    fun reduceToEffect(action: A, state: S): E?
}