package ru.gorshenev.themesstyles.baseRecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.ViewTyped

abstract class BaseViewHolder<T : ViewTyped>(containerView: View) :
    RecyclerView.ViewHolder(containerView) {

    open fun bind(item: T) = Unit
}