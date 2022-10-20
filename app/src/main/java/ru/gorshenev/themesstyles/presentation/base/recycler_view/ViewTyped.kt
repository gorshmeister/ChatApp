package ru.gorshenev.themesstyles.presentation.base.recycler_view

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
    val id: Int
        get() = error("provide id $this")
}
