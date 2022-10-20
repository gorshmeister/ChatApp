package ru.gorshenev.themesstyles.presentation.base.recycler_view

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : ViewTyped>(containerView: View) :
    RecyclerView.ViewHolder(containerView) {

    open fun bind(item: T) = Unit
}
