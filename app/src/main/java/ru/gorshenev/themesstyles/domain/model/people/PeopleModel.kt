package ru.gorshenev.themesstyles.domain.model.people

import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

data class PeopleModel(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String,
    val status: PeopleUi.PeopleStatus = PeopleUi.PeopleStatus.OFFLINE,
)