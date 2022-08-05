package ru.gorshenev.themesstyles

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
    val id: Int
        get() = error("provide id $this")
}
