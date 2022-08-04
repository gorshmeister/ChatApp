package ru.gorshenev.themesstyles.hw3.items

data class EmojiUi(
    val code: Int,
    val counter: Int = 0,
    val isSelected: Boolean = false,
    val message_id: Int = 0,
    val user_id: List<Int> = emptyList()
) {

    companion object {
        const val PLUS_CODE: Int = 0
    }
}
