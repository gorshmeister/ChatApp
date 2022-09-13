package ru.gorshenev.themesstyles.data.repositories

import io.reactivex.Single
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import kotlin.random.Random

object ChatDataSource {



//    fun getMessage(count: Int) =
//        Single.fromCallable { createMessages(count) }
//
//    private fun createMessages(count: Int): List<ViewTyped> {
//        var id = 0
//        val name = PeopleDataSource.getRandomName()
//        val messages = mutableListOf<ViewTyped>()
//
//        for (i in 1..count) {
//            messages += DateUi(id = ++id, text = "$i Feb")
//            messages += MessageLeftUi(
//                id = ++id,
//                name = name,
//                text = "Text Text Text Text Text Text Text Text Text Text Text ",
//                time = "11:11",
//                emojis = ReactionsData.getFakeEmojis(Random.nextInt(5))
//            )
//            messages+= MessageRightUi(
//                id = ++id,
//                text = "Text Text Text Text Text Text Text Text Text Text Text ",
//                time = "11:11",
//                emojis = ReactionsData.getFakeEmojis(Random.nextInt(5))
//            )
//        }
//        return messages.toList()
//    }


}