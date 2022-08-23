package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ItemDateBinding
import ru.gorshenev.themesstyles.items.DateUi


class DateViewHolder(
    view: View,
) : BaseViewHolder<DateUi>(view) {
    private val binding: ItemDateBinding by viewBinding()

    override fun bind(item: DateUi) {
        binding.tvDate.text = item.text
    }
}
