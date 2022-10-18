package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemDateBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi


class DateViewHolder(
    view: View,
) : BaseViewHolder<DateUi>(view) {
    private val binding: ItemDateBinding by viewBinding()

    override fun bind(item: DateUi) {
        binding.tvDate.text = item.text
    }
}
