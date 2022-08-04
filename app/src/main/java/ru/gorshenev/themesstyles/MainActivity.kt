package ru.gorshenev.themesstyles

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.text.trimmedLength
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.rv.HolderFactory
import ru.gorshenev.themesstyles.hw3.Adapter
import ru.gorshenev.themesstyles.hw3.Data
import ru.gorshenev.themesstyles.hw3.ViewTyped
import ru.gorshenev.themesstyles.hw3.holderFactory.TfsHolderFactory
import ru.gorshenev.themesstyles.hw3.items.EmojiUi
import ru.gorshenev.themesstyles.hw3.items.LeftMessageUi
import ru.gorshenev.themesstyles.hw3.items.RightMessageUi
import ru.gorshenev.themesstyles.hw3.items.TextUi
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var messageField: EditText
    private lateinit var btnSend: ImageView

    private val bottomSheet: BottomSheet = BottomSheet()

    private val holderFactory: HolderFactory = TfsHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.MSG_ID to messageId)
            bottomSheet.show(supportFragmentManager, BottomSheet.TAG)
            true
        },
        onEmojiClick = { emojiCode, messageId ->
            adapter.items = updateEmojis(adapter.items, emojiCode, messageId)
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)

    private fun updateEmojis(messages: List<ViewTyped>, emojiCode: Int, messageId: Int): List<ViewTyped> {
        return messages.map { item ->
            when (item) {
                is RightMessageUi -> {
                    val updatedEmojis = item.emojis.map {
                        val isTargetEmoji = it.code == emojiCode && item.id == messageId
                        val isMeClicked = it.user_id.contains(Data.MY_USER_ID)
                        when {
                            isTargetEmoji && !isMeClicked -> {
                                it.copy(
                                    isSelected = true,
                                    user_id = it.user_id + listOf(Data.MY_USER_ID),
                                    counter = it.counter + 1
                                )
                            }
                            isTargetEmoji && isMeClicked -> {
                                it.copy(
                                    isSelected = false,
                                    user_id = it.user_id - listOf(Data.MY_USER_ID),
                                    counter = it.counter - 1
                                )
                            }
                            else -> it
                        }
                    }
                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                is LeftMessageUi -> {
                    val updatedEmojis = item.emojis.map {
                        if (it.code == emojiCode && item.id == messageId && !it.user_id.contains(
                                Data.MY_USER_ID
                            )
                        ) {
                            it.copy(
                                isSelected = true,
                                user_id = it.user_id + listOf(Data.MY_USER_ID),
                                counter = it.counter + 1
                            )
                        } else if (it.code == emojiCode && item.id == messageId && it.user_id.contains(
                                Data.MY_USER_ID
                            )
                        ) {
                            it.copy(
                                isSelected = false,
                                user_id = it.user_id - listOf(Data.MY_USER_ID),
                                counter = it.counter - 1
                            )
                        } else {
                            it
                        }
                    }
                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                else -> item
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.setFragmentResultListener(
            BottomSheet.PICKER_KEY,
            this
        ) { _, result ->
            val (messageId, emojiCode) = result.get(BottomSheet.EMOJI_PICK) as BottomSheet.EmojiPickResult
            adapter.items = adapter.items.map { item ->
                when (item) {
                    is RightMessageUi -> {
                        if (item.id == messageId) {
                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                            if (isEmojiExists) {
                                Toast.makeText(this, "Реакция уже существует!", Toast.LENGTH_SHORT)
                                    .show()
                                item
                            } else {
                                item.copy(
                                    emojis = item.emojis + EmojiUi(
                                        code = emojiCode,
                                        counter = 1,
                                        isSelected = true,
                                        message_id = messageId,
                                        user_id = listOf(Data.MY_USER_ID),
                                    )
                                )
                            }
                        } else {
                            item
                        }
                    }
                    is LeftMessageUi -> {
                        if (item.id == messageId) {
                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                            if (isEmojiExists) {
                                Toast.makeText(this, "Реакция уже существует!", Toast.LENGTH_SHORT)
                                    .show()
                                item
                            } else {
                                item.copy(
                                    emojis = item.emojis + EmojiUi(
                                        code = emojiCode,
                                        counter = 1,
                                        isSelected = true,
                                        message_id = messageId,
                                        user_id = listOf(Data.MY_USER_ID),
                                    )
                                )
                            }
                        } else {
                            item
                        }
                    }
                    else -> item
                }
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_items)
        adapter.items = listOf(
            TextUi(id = 1, text = "1 Feb", viewType = R.layout.item_date),
            LeftMessageUi(
                id = 2,
                name = "Name Surname",
                text = "Text Text Text Text Text Text Text Text Text Text Text ",
                time = "11:11",
                emojis = Data.getFakeEmojis()
            ),
            RightMessageUi(
                id = 3,
                text = "Text Text Text Text Text Text Text Text Text Text Text ",
                time = "11:11",
                emojis = Data.getFakeEmojis()
            ),
        )
        recyclerView.adapter = adapter

        messageField = findViewById(R.id.et_msgField)
        btnSend = findViewById(R.id.btn_sendMsg)
        inputField()

        //todo govnoCode
        btnSend.setOnClickListener {
            if (messageField.text.isNotBlank()) {
                val lastDate = (adapter.items.findLast { it is TextUi } as TextUi).text
                if (lastDate != getDate()) {
                    adapter.items += TextUi(
                        id = adapter.itemCount + 1,
                        text = getDate(),
                        viewType = R.layout.item_date
                    )
                }

//                val listOfDates = adapter.items.filter { viewTyped ->
//                    viewTyped.viewType == R.layout.item_date
//                }
//                val isCurrentDate = listOfDates.any {
//                    val item = (it as TextUi)
//                    item.text == getDate()
//                }
//
//                if (!isCurrentDate)
//                    adapter.items += TextUi(
//                        id = adapter.itemCount + 1,
//                        text = getDate(),
//                        viewType = R.layout.item_date
//                    )

//                val lastView = adapter.items.lastOrNull() { viewTyped ->
//                    viewTyped.viewType == R.layout.item_date
//                }
//                when (lastView) {
//                    null -> adapter.items += TextUi(
//                        id = adapter.itemCount + 1,
//                        text = getDate(),
//                        viewType = R.layout.item_date
//                    )
//                    is TextUi ->
//                        if (lastView.text != getDate()) {
//                            adapter.items += TextUi(
//                                id = adapter.itemCount + 1,
//                                text = getDate(),
//                                viewType = R.layout.item_date
//                            )
//                        }
//                }

                //todo fix recyclerView size
                adapter.items += RightMessageUi(
                    id = adapter.itemCount + 1,
                    text = messageField.text.toString(),
                    time = getTime(),
                    emojis = emptyList()
                )
            }
            recyclerView.smoothScrollToPosition(adapter.itemCount)

//            recyclerView.smoothSnapToPosition(LinearSmoothScroller.SNAP_TO_ANY)


            messageField.text.clear()
        }
    }

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_END,
    ) {

        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun inputField() {
        messageField.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                btnSend.setImageResource(R.drawable.plane)
            else
                btnSend.setImageResource(R.drawable.plus)
        }
    }

    private fun getTime(): String {
        val formatter = SimpleDateFormat("kk:mm", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }
}