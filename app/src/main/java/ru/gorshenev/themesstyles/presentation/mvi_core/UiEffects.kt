package ru.gorshenev.themesstyles.presentation.mvi_core

sealed class UiEffects {
    class SnackBar(val error: Throwable) : UiEffects()
}