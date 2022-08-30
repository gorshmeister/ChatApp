package ru.gorshenev.themesstyles

import io.reactivex.Observable
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.items.DateUi
import ru.gorshenev.themesstyles.items.MessageLeftUi
import ru.gorshenev.themesstyles.items.MessageRightUi
import kotlin.random.Random

object ChatDataSource {

    fun getMessageObservable(count: Int) =
        Observable.fromCallable { createMessages(count) }

    private fun createMessages(count: Int): List<ViewTyped> {
        var id = 0
        val name = PeopleDataSource.getRandomName()
        val messages = mutableListOf<ViewTyped>()

        for (i in 1..count) {
            messages += DateUi(id = ++id, text = "$i Feb")
            messages += MessageLeftUi(
                id = ++id,
                name = name,
                text = "Text Text Text Text Text Text Text Text Text Text Text ",
                time = "11:11",
                emojis = ReactionsData.getFakeEmojis(Random.nextInt(5))
            )
            messages+= MessageRightUi(
                id = ++id,
                text = "Text Text Text Text Text Text Text Text Text Text Text ",
                time = "11:11",
                emojis = ReactionsData.getFakeEmojis(Random.nextInt(5))
            )
        }
        return messages.toList()
    }


}