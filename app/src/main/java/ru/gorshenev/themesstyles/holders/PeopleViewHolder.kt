package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemPeopleBinding
import ru.gorshenev.themesstyles.items.PeopleUi

class PeopleViewHolder(
    view: View
) : BaseViewHolder<PeopleUi>(view) {
private val binding: ItemPeopleBinding by viewBinding()

    override fun bind(item: PeopleUi) {
        with(binding) {
            ivPeopleAvatar.setImageResource(item.avatar)
            tvPeopleName.text = item.name
            tvPeopleEmail.text = item.email
        }
    }
}