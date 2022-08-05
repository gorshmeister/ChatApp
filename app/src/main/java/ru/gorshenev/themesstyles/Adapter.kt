package ru.gorshenev.themesstyles

import ru.gorshenev.themesstyles.baseRecyclerView.BaseAdapter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory

class Adapter<T : ViewTyped>(holderFactory: HolderFactory) : BaseAdapter<T>(holderFactory) {
    private val localItems: MutableList<T> = mutableListOf()

    override var items: List<T>
        get() = localItems
        set(newItems) {
            localItems.clear()
            localItems.addAll(newItems)
            notifyDataSetChanged()
        }
}