package ru.gorshenev.themesstyles.presentation.base.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.ui.chat.adapter.DateViewHolder

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped> {

    companion object {
        const val UNKNOWN_VIEW_TYPE = "Unknown viewType = "
    }

    abstract fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>?

    override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view: View = viewGroup.inflate(viewType)

        return when (viewType) {
            R.layout.item_date -> DateViewHolder(view)
            else -> checkNotNull(createViewHolder(view, viewType)) {
                UNKNOWN_VIEW_TYPE + viewGroup.resources.getResourceName(viewType)
            }
        } as BaseViewHolder<ViewTyped>
    }
}

fun <T : View> View.inflate(
    layout: Int,
    root: ViewGroup? = this as? ViewGroup,
    attachToRoot: Boolean = false,
): T {
    return LayoutInflater.from(context).inflate(layout, root, attachToRoot) as T
}
