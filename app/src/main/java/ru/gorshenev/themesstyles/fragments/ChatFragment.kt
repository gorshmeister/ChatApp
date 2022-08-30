package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.*
import ru.gorshenev.themesstyles.StreamMapper.addReactions
import ru.gorshenev.themesstyles.StreamMapper.updateEmojisCounter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.databinding.FragmentChatBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.holderFactory.ChatHolderFactory
import ru.gorshenev.themesstyles.items.DateUi
import ru.gorshenev.themesstyles.items.LeftMessageUi
import ru.gorshenev.themesstyles.items.RightMessageUi

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val binding: FragmentChatBinding by viewBinding()

    private val bottomSheet: BottomSheet = BottomSheet()

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            if (bottomSheet !in parentFragmentManager.fragments) {
                bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                true
            }
        },
        onEmojiClick = { emojiCode, messageId ->
            adapter.items = updateEmojisCounter(adapter.items, emojiCode, messageId)
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            getColor(requireContext(), R.color.colorPrimaryBlue)

        parentFragmentManager.setFragmentResultListener(BottomSheet.PICKER_KEY, this) { _, result ->
            val (messageId, emojiCode) = result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult

            try {
                adapter.items = addReactions(adapter.items, messageId, emojiCode)
            } catch (e: StreamMapper.ReactionAlreadyExist) {
                Toast.makeText(
                    context,
                    "Реакция уже существует!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
        binding.rvItems.adapter = adapter

        inputField()

        with(binding) {
            btnSendMsg.setOnClickListener {
                if (etMsgField.text.isNotBlank()) {
                    val lastDate = (adapter.items.findLast { it is DateUi } as DateUi).text
                    if (lastDate != Utils.getCurrentDate()) {
                        adapter.items += DateUi(
                            id = adapter.itemCount + 1,
                            text = Utils.getCurrentDate(),
                        )
                    }

                    adapter.items += RightMessageUi(
                        id = adapter.itemCount + 1,
                        text = etMsgField.text.toString(),
                        time = Utils.getCurrentTime(),
                        emojis = emptyList()
                    )
                }

                rvItems.smoothScrollToPosition(adapter.itemCount)
                etMsgField.text.clear()
            }

            tvTopicName.text = "Topic: #${requireArguments().getString(TPC_NAME, "topic")}"
            toolbar.title = requireArguments().getString(STR_NAME, "stream")
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
                requireActivity().window.statusBarColor =
                    getColor(requireContext(), R.color.colorPrimaryBlack)
            }
        }
    }

    private fun inputField() {
        with(binding) {
            etMsgField.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrBlank())
                    btnSendMsg.setImageResource(R.drawable.icon_input_field_plane)
                else
                    btnSendMsg.setImageResource(R.drawable.icon_input_field_plus)
            }
        }
    }
}