package ru.gorshenev.themesstyles.data.repositories.people

import ru.gorshenev.themesstyles.domain.model.people.PeopleModel
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

object PeopleMapper {

    fun List<PeopleModel>.toUi(): List<PeopleUi> {
        return this.map {
            PeopleUi(
                id = it.id,
                name = it.name,
                email = it.email,
                avatar = it.avatar,
                status = it.status
            )
        }
    }
}