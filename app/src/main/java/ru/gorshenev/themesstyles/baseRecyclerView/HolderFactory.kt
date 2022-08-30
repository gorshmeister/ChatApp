package ru.gorshenev.themesstyles.baseRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.holders.DateViewHolder

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped> {

    abstract fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>?

    override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view: View = viewGroup.inflate(viewType)

        return when (viewType) {
            R.layout.item_date -> DateViewHolder(view)
            else -> checkNotNull(createViewHolder(view, viewType)) {
                "unknown viewType=" + viewGroup.resources.getResourceName(viewType)
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
