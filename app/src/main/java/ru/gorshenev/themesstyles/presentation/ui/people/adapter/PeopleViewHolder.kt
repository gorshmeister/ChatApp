package ru.gorshenev.themesstyles.presentation.ui.people.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ItemPeopleBinding
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

class PeopleViewHolder(
    view: View
) : BaseViewHolder<PeopleUi>(view) {
    private val binding: ItemPeopleBinding by viewBinding()

    override fun bind(item: PeopleUi) {
        with(binding) {
            Glide.with(itemView).load(item.avatar).into(ivPeopleAvatar)
            tvPeopleName.text = item.name
            tvPeopleEmail.text = item.email
            indicator.setBackgroundResource(
                when (item.status) {
                    PeopleUi.PeopleStatus.ONLINE -> R.drawable.bg_people_indicator_on
                    PeopleUi.PeopleStatus.IDLE -> R.drawable.bg_people_indicator_idle
                    PeopleUi.PeopleStatus.OFFLINE -> R.drawable.bg_people_indicator_off
                }
            )
        }
    }
}