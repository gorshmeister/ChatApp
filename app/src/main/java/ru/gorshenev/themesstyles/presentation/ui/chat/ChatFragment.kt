package ru.gorshenev.themesstyles.presentation.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.*
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.data.mappers.StreamMapper
import ru.gorshenev.themesstyles.data.repositories.ChatDataSource
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentChatBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.BottomSheet.Companion.PICKER_KEY
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamView
import ru.gorshenev.themesstyles.presentation.ui.chat.adapter.ChatHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import kotlin.random.Random

class ChatFragment : Fragment(R.layout.fragment_chat), ChatView {
    private val binding: FragmentChatBinding by viewBinding()

    private val bottomSheet: BottomSheet = BottomSheet()

    private val presenter: ChatPresenter = ChatPresenter(this)

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            if (bottomSheet !in parentFragmentManager.fragments) {
                bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                true
            }
        },
        onEmojiClick = { emojiCode, messageId ->
            presenter.onEmojiClick(emojiCode, messageId)
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initInputField()
        initSendingMessages()
        initStreamAndTopicNames()
        presenter.loadMessages(Random.nextInt(24))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onClear()
    }


    private fun initViews() {
        with(binding) {
            requireActivity().window.statusBarColor =
                getColor(requireContext(), R.color.colorPrimaryBlue)

            rvItems.adapter = adapter

            parentFragmentManager.setFragmentResultListener(PICKER_KEY, this@ChatFragment) { _, result ->
                val resultPick =
                    result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult
                presenter.addReaction(resultPick)
            }
        }
    }

    private fun initInputField() {
        with(binding) {
            etMsgField.addTextChangedListener { text ->
                if (!text.isNullOrBlank())
                    btnSendMsg.setImageResource(R.drawable.icon_input_field_plane)
                else
                    btnSendMsg.setImageResource(R.drawable.icon_input_field_plus)
            }
        }
    }

    private fun initSendingMessages() {
        with(binding) {
            btnSendMsg.setOnClickListener {
                if (etMsgField.text.isNotBlank()) {
                    presenter.sendMessage(etMsgField.text.toString())
                }

                rvItems.smoothScrollToPosition(adapter.itemCount)
                etMsgField.text.clear()
            }
        }
    }

    private fun initStreamAndTopicNames() {
        with(binding) {
            val topicName = requireArguments().getString(TPC_NAME, "topic")
            toolbar.title = requireArguments().getString(STR_NAME, "stream")
            tvTopicName.text = "Topic: #$topicName"
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
                requireActivity().window.statusBarColor =
                    getColor(requireContext(), R.color.colorPrimaryBlack)
            }
        }
    }

    override fun showToast() {
        Toast.makeText(context, "Реакция уже существует!", Toast.LENGTH_SHORT).show()
    }

    override fun showItems(items: List<ViewTyped>) {
        adapter.items = items
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(
            binding.root, """Something wrong! $error
            |${error?.message}""".trimMargin(), Snackbar.LENGTH_SHORT
        ).show()
    }

}
