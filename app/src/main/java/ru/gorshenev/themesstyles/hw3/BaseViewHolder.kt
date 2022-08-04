package ru.gorshenev.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.hw3.ViewTyped

abstract class BaseViewHolder<T : ViewTyped>(containerView: View) :
    RecyclerView.ViewHolder(containerView) {

    open fun bind(item: T) = Unit
}
