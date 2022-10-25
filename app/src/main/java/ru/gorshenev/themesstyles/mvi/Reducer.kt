package ru.gorshenev.themesstyles.mvi

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}