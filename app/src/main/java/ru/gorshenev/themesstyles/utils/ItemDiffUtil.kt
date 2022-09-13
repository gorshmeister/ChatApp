package ru.gorshenev.themesstyles.utils

import androidx.recyclerview.widget.DiffUtil
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.ReactionsUi
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

class ItemDiffUtil(
    private val oldList: List<ViewTyped>,
    private val newList: List<ViewTyped>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            (oldItem is DateUi) && (newItem is DateUi) -> oldItem.text == newItem.text

            (oldItem is MessageLeftUi) && (newItem is MessageLeftUi) -> oldItem.avatar == newItem.avatar
                    && oldItem.name == newItem.name
                    && oldItem.text == newItem.text
                    && oldItem.emojis == newItem.emojis

            (oldItem is MessageRightUi) && (newItem is MessageRightUi) -> oldItem.text == newItem.text
                    && oldItem.emojis == newItem.emojis

            (oldItem is PeopleUi) && (newItem is PeopleUi) -> oldItem.avatar == newItem.avatar
                    && oldItem.status == newItem.status
                    && oldItem.name == newItem.name
                    && oldItem.email == newItem.email

            (oldItem is ReactionsUi) && (newItem is ReactionsUi) -> oldItem.name == newItem.name
                    && oldItem.code == newItem.code
                    && oldItem.category == newItem.category

            (oldItem is StreamUi) && (newItem is StreamUi) -> oldItem.name == newItem.name
                    &&oldItem.isExpanded == newItem.isExpanded
                    &&oldItem.topics == newItem.topics

            (oldItem is TopicUi) && (newItem is TopicUi) -> oldItem.name == newItem.name
                    && oldItem.color == newItem.color


            else -> throw error("DiffUtil problem")
        }
    }

}