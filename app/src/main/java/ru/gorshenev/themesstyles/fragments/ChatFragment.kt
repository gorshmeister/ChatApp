package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.*
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holderFactory.TfsHolderFactory
import ru.gorshenev.themesstyles.items.EmojiUi
import ru.gorshenev.themesstyles.items.LeftMessageUi
import ru.gorshenev.themesstyles.items.RightMessageUi
import ru.gorshenev.themesstyles.items.TextUi

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var messageField: EditText
    private lateinit var btnSend: ImageView

    private val bottomSheet: BottomSheet = BottomSheet()

    private val holderFactory: HolderFactory = TfsHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARGUMENT_MSG_ID to messageId)
            bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
            true
        },
        onEmojiClick = { emojiCode, messageId ->
            adapter.items = updateEmojis(adapter.items, emojiCode, messageId)
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)

    private fun updateEmojis(
        messages: List<ViewTyped>,
        emojiCode: Int,
        messageId: Int
    ): List<ViewTyped> {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(BottomSheet.PICKER_KEY, this) { _, result ->
            val (messageId, emojiCode) = result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult

            adapter.items = adapter.items.map { item ->
                when (item) {
                    is RightMessageUi -> {
                        if (item.id == messageId) {
                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                            if (isEmojiExists) {
                                Toast.makeText(
                                    context,
                                    "Реакция уже существует!",
                                    Toast.LENGTH_SHORT
                                )
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
                                Toast.makeText(
                                    context,
                                    "Реакция уже существует!",
                                    Toast.LENGTH_SHORT
                                )
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_items)
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

        messageField = view.findViewById(R.id.et_msgField)
        btnSend = view.findViewById(R.id.btn_sendMsg)
        inputField()

        btnSend.setOnClickListener {
            if (messageField.text.isNotBlank()) {
                val lastDate = (adapter.items.findLast { it is TextUi } as TextUi).text
                if (lastDate != Utils.getCurrentDate()) {
                    adapter.items += TextUi(
                        id = adapter.itemCount + 1,
                        text = Utils.getCurrentDate(),
                        viewType = R.layout.item_date
                    )
                }

                adapter.items += RightMessageUi(
                    id = adapter.itemCount + 1,
                    text = messageField.text.toString(),
                    time = Utils.getCurrentTime(),
                    emojis = emptyList()
                )
            }

            recyclerView.smoothScrollToPosition(adapter.itemCount)
            messageField.text.clear()
        }

        //todo как правильно вернуться, как установить картинку на тулбар
        view.findViewById<Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, ChannelsFragment())
                    .commit()
            }
    }

    private fun inputField() {
        messageField.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank())
                btnSend.setImageResource(R.drawable.icon_input_field_plane)
            else
                btnSend.setImageResource(R.drawable.icon_input_field_plus)
        }
    }
}

