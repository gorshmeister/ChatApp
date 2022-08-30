package ru.gorshenev.themesstyles.baseRecyclerView

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
    val id: Int
        get() = error("provide id $this")
}
