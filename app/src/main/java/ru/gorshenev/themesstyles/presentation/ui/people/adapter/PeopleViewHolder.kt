package ru.gorshenev.themesstyles.presentation.ui.people.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemPeopleBinding
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

class PeopleViewHolder(
    view: View
) : BaseViewHolder<PeopleUi>(view) {
private val binding: ItemPeopleBinding by viewBinding()

    override fun bind(item: PeopleUi) {
        with(binding) {
            Glide.with(itemView).load(item.avatar).into(ivPeopleAvatar)
//            ivPeopleAvatar.setImageResource(item.avatar)
            tvPeopleName.text = item.name
            tvPeopleEmail.text = item.email
            when (item.status) {
                PeopleUi.PeopleStatus.ONLINE -> indicator.setBackgroundResource(R.drawable.bg_people_indicator_on)
                PeopleUi.PeopleStatus.IDLE -> indicator.setBackgroundResource(R.drawable.bg_people_indicator_idle)
                PeopleUi.PeopleStatus.OFFLINE -> indicator.setBackgroundResource(R.drawable.bg_people_indicator_off)
            }
        }
    }
}