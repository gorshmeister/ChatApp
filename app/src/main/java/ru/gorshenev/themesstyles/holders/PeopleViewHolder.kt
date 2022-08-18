package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.items.PeopleUi

class PeopleViewHolder(
    view: View
) : BaseViewHolder<PeopleUi>(view) {

    val avatar: ImageView = view.findViewById(R.id.iv_people_avatar)
    val name: TextView = view.findViewById(R.id.tv_people_name)
    val email: TextView = view.findViewById(R.id.tv_people_email)

    override fun bind(item: PeopleUi) {
        avatar.setImageResource(item.avatar)
        name.text = item.name
        email.text = item.email
    }
}