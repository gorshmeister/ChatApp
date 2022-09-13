package ru.gorshenev.themesstyles.data.repositories

import io.reactivex.Observable
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object PeopleDataSource {

    fun getPeople(count: Int): Observable<List<PeopleUi>> =
        Observable.fromCallable { createPeople(count) }
            .delay (2, TimeUnit.SECONDS)


    private fun createPeople(count: Int): List<PeopleUi> {
        return List(count) {

            val n = Random.nextInt(names.size - 1)
            val m = Random.nextInt(surnames.size - 1)
            PeopleUi(
                id = it,
                name = "${names[n]} ${surnames[m]}",
                email = "${surnames[m]}@gmail.com",
                isOnline = it % 2 == 0
            )
        }
    }

    fun getRandomName(): String {
        val n = Random.nextInt(names.size - 1)
        val m = Random.nextInt(surnames.size - 1)
        return "${names[n]} ${surnames[m]}"
    }

    private val names: List<String> = listOf(
        "Leela",
        "Hayden",
        "Sunil",
        "Rita",
        "Cayson",
        "Benjamin",
        "Vinnie",
        "Ayah",
        "Hana",
        "Levison",
        "Jared",
        "Kristie",
        "Christina",
        "Faizan",
        "Azeem",
        "Malika",
        "Haseeb",
        "Kaci",
        "Trixie",
        "Bryan",
    )
    private val surnames: List<String> = listOf(
        "Williams",
        "Mccabe",
        "Ortega",
        "Alford",
        "Huerta",
        "York",
        "Lara",
        "Sweeney",
        "Faulkner",
        "Senior",
        "Blankenship",
        "Berg",
        "Grimes",
        "Wardle",
        "Koch",
        "Chandler",
        "Cano",
        "Aguirre",
        "Giles",
        "Vu",
    )

}