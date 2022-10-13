package ru.gorshenev.themesstyles.presentation.base_recycler_view

import androidx.recyclerview.widget.DiffUtil
import ru.gorshenev.themesstyles.utils.ItemDiffUtil

class Adapter<T : ViewTyped>(holderFactory: HolderFactory) : BaseAdapter<T>(holderFactory) {
    private val localItems: MutableList<T> = mutableListOf()

    override var items: List<T>
        get() = localItems
        set(newItems) {
            val itemDiffUtilCallback = ItemDiffUtil(localItems, newItems)
            val itemDiffResult = DiffUtil.calculateDiff(itemDiffUtilCallback,true)
            localItems.clear()
            localItems.addAll(newItems)
            itemDiffResult.dispatchUpdatesTo(this)
        }

}