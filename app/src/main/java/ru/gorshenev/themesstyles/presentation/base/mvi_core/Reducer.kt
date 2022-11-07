package ru.gorshenev.themesstyles.presentation.base.mvi_core

import java.util.*

interface Reducer<A, S, E> {
    fun reduceToState(action: A, state: S): S
    fun reduceToEffect(action: A, state: S): Optional<E>
}