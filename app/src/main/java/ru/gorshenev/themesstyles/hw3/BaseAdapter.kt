package ru.gorshenev.themesstyles.hw3

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.rv.BaseViewHolder
import ru.gorshenev.rv.HolderFactory


abstract class BaseAdapter<T : ViewTyped>(internal val holderFactory: HolderFactory) :
    RecyclerView.Adapter<BaseViewHolder<ViewTyped>>() {

    abstract var items: List<T>

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        return holderFactory(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewTyped>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

