package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.*
import ru.gorshenev.themesstyles.StreamMapper.addReactions
import ru.gorshenev.themesstyles.StreamMapper.updateEmojisCounter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.holderFactory.ChatHolderFactory
import ru.gorshenev.themesstyles.items.DateUi
import ru.gorshenev.themesstyles.items.EmojiUi
import ru.gorshenev.themesstyles.items.LeftMessageUi
import ru.gorshenev.themesstyles.items.RightMessageUi

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var messageField: EditText
    private lateinit var btnSend: ImageView

    private val bottomSheet: BottomSheet = BottomSheet()

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
            true
        },
        onEmojiClick = { emojiCode, messageId ->
            adapter.items = updateEmojisCounter(adapter.items, emojiCode, messageId)
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(BottomSheet.PICKER_KEY, this) { _, result ->
            val (messageId, emojiCode) = result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult

            adapter.items = addReactions(adapter.items, messageId, emojiCode, context)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_items)
        adapter.items = listOf(
            DateUi(id = 1, text = "1 Feb"),
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
                val lastDate = (adapter.items.findLast { it is DateUi } as DateUi).text
                if (lastDate != Utils.getCurrentDate()) {
                    adapter.items += DateUi(
                        id = adapter.itemCount + 1,
                        text = Utils.getCurrentDate(),
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
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val underToolbar = view.findViewById<TextView>(R.id.tv_topic_name)
        underToolbar.text = "Topic: #${requireArguments().getString(TPC_NAME, "topic")}"
        toolbar.title = requireArguments().getString(STR_NAME, "stream")
        toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
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

