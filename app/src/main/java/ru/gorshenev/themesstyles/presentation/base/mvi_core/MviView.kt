package ru.gorshenev.themesstyles.presentation.base.mvi_core

interface MviView<in S, in E> {
    fun render(state: S)
    fun handleUiEffects(effect: E)
}