package ru.gorshenev.themesstyles.presentation.mvi_core

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}