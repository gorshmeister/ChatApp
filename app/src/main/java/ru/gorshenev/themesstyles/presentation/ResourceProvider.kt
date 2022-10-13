package ru.gorshenev.themesstyles.presentation

import android.content.Context
import androidx.annotation.ColorRes

class ResourceProvider(private val context: Context) {

    fun getColor(@ColorRes resId: Int) = context.getColor(resId)
}