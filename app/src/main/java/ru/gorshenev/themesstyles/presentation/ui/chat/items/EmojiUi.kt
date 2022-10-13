package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.data.repositories.chat.Reactions

data class EmojiUi(
    val msgId: Int,
    val name: String,
    val code: Int,
    val listUsersId: List<Int> = emptyList(),
) {
    val counter: Int
        get() = listUsersId.size

    val isSelected: Boolean
        get() = listUsersId.contains(Reactions.MY_USER_ID)
}

