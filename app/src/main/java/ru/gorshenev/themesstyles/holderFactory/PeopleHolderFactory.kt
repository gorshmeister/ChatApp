package ru.gorshenev.themesstyles.holderFactory

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holders.PeopleViewHolder

class PeopleHolderFactory : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_people -> PeopleViewHolder(view)
            else -> null
        }
    }

}
