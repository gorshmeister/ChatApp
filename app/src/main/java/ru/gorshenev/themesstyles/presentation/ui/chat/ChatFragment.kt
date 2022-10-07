package ru.gorshenev.themesstyles.presentation.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentChatBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.chat.BottomSheet.Companion.PICKER_KEY
import ru.gorshenev.themesstyles.presentation.ui.chat.adapter.ChatHolderFactory

class ChatFragment : Fragment(R.layout.fragment_chat), ChatView {
    private val binding: FragmentChatBinding by viewBinding()

    private val bottomSheet: BottomSheet = BottomSheet()

    private val presenter: ChatPresenter = ChatPresenter(this)

    private val topicName: String by lazy { arguments?.getString(TPC_NAME).toString() }

    private val streamName: String by lazy { arguments?.getString(STR_NAME).toString() }

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            if (bottomSheet !in parentFragmentManager.fragments) {
                bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                true
            }
        },
        onEmojiClick = { emojiName, _, messageId ->
            presenter.onEmojiClick(emojiName, messageId)
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initInputField()
        initSendingMessages()
        initStreamAndTopicNames()
        presenter.loadMessages(streamName, topicName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onClear()
    }


    private fun initViews() {
        with(binding) {
            requireActivity().window.statusBarColor =
                getColor(requireContext(), R.color.colorPrimaryBlue)

            val layoutManager = rvItems.layoutManager as? LinearLayoutManager

            rvItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val position = layoutManager?.findFirstVisibleItemPosition() ?: 0
                    if (position == 5 && dy != 0) {
                        presenter.uploadMoreMessages()
                    }
                }
            })

            rvItems.adapter = adapter

            parentFragmentManager.setFragmentResultListener(
                PICKER_KEY,
                this@ChatFragment
            ) { _, result ->
                val resultPick =
                    result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult
                presenter.onEmojiClick(
                    emojiName = resultPick.emojiName,
                    messageId = resultPick.messageId
                )
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
                etMsgField.text.clear()
            }
        }
    }


    private fun initStreamAndTopicNames() {
        with(binding) {
            toolbar.title = streamName
            tvTopicName.text = getString(R.string.topic_name, topicName)

            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        onBackPress()
                    }
                })
            toolbar.setNavigationOnClickListener {
                onBackPress()
            }
        }
    }

    private fun onBackPress() {
        parentFragmentManager.popBackStack()
        requireActivity().window.statusBarColor =
            getColor(requireContext(), R.color.colorPrimaryBlack)
    }

    override fun scrollMsgsToTheEnd() {
        binding.rvItems.smoothScrollToPosition(adapter.itemCount)
    }

    override fun showToast() {
        Toast.makeText(context, "Реакция уже существует!", Toast.LENGTH_SHORT).show()
    }

    override fun showItems(items: List<ViewTyped>) {
        with(binding) {
            if (items.isEmpty()) {
                emptyState.tvEmptyState.isVisible = true
                rvItems.isGone = true
            } else {
                emptyState.tvEmptyState.isGone = true
                rvItems.isVisible = true
                adapter.items = items
            }
        }
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
        Log.d("qweqwe", "CHAT PROBLEM $error")
    }

    override fun showLoading() {
        binding.shimmerChat.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    override fun stopLoading() {
        binding.shimmerChat.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}
