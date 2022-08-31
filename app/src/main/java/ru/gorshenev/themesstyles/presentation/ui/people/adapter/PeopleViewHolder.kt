package ru.gorshenev.themesstyles.presentation.ui.people.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemPeopleBinding
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

class PeopleViewHolder(
    view: View
) : BaseViewHolder<PeopleUi>(view) {
private val binding: ItemPeopleBinding by viewBinding()

    override fun bind(item: PeopleUi) {
        with(binding) {
            ivPeopleAvatar.setImageResource(item.avatar)
            tvPeopleName.text = item.name
            tvPeopleEmail.text = item.email
            indicator.isSelected = item.isOnline
        }
    }
}