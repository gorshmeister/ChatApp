package ru.gorshenev.themesstyles.presentation.base_recycler_view

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
    val id: Int
        get() = error("provide id $this")
}
