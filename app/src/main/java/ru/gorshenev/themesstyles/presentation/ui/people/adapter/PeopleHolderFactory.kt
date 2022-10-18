package ru.gorshenev.themesstyles.presentation.ui.people.adapter

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory

class PeopleHolderFactory : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_people -> PeopleViewHolder(view)
            else -> null
        }
    }

}
