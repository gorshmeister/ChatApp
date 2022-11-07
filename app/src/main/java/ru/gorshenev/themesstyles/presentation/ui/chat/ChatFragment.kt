package ru.gorshenev.themesstyles.presentation.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.databinding.FragmentChatBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviView
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModel
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModelFactory
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.chat.BottomSheet.Companion.PICKER_KEY
import ru.gorshenev.themesstyles.presentation.ui.chat.adapter.ChatHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.chat.middleware.*
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class
ChatFragment : Fragment(R.layout.fragment_chat),
    MviView<ChatState, ChatEffect> {
    private val binding: FragmentChatBinding by viewBinding()

    private val topicName: String by lazy { arguments?.getString(TPC_NAME).toString() }

    private val streamName: String by lazy { arguments?.getString(STR_NAME).toString() }

    private val bottomSheet: BottomSheet = BottomSheet()

    private val repository = GlobalDI.INSTANSE.chatRepository

    private val chatViewModel: MviViewModel<ChatAction, ChatState, ChatEffect> by viewModels {
        val chatStore: Store<ChatAction, ChatState, ChatEffect> =
            Store(
                reducer = ChatReducer(),
                middlewares = listOf(
                    UploadMiddleware(repository),
                    UploadMoreMiddleware(repository),
                    SendMessageMiddleware(repository),
                    OnEmojiClickMiddleware(repository),
                    RegisterMessageQueueMiddleware(repository),
                    GetQueueMessageMiddleware(repository),
                    RegisterReactionQueueMiddleware(repository),
                    GetQueueReactionMiddleware(repository)
                ),
                initialState = ChatState.Loading
            )
        MviViewModelFactory(chatStore)
    }

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            if (bottomSheet !in parentFragmentManager.fragments) {
                bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                true
            }
        },
        onEmojiClick = { emojiName, _, messageId ->
            chatViewModel.accept(ChatAction.OnEmojiClick(emojiName, messageId, false))
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initInputField()
        initSendingMessages()
        initStreamAndTopicNames()
        chatViewModel.bind(this)
        chatViewModel.accept(ChatAction.UploadMessages(streamName, topicName))
    }


    private fun initViews() {
        with(binding) {
            this@ChatFragment.setStatusBarColor(R.color.color_primary)

            val layoutManager = rvItems.layoutManager as? LinearLayoutManager

            rvItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val position = layoutManager?.findFirstVisibleItemPosition() ?: 0
                    if (position == START_LOADING_POSITION && dy != ZERO_SCROLL_POSITION) {
                        progress(true)
                        chatViewModel.accept(
                            ChatAction.UploadMoreMessages(streamName, topicName)
                        )
                    }
                }
            })

            rvItems.adapter = adapter

            parentFragmentManager.setFragmentResultListener(PICKER_KEY, this@ChatFragment)
            { _, result ->
                val resultPick =
                    result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult
                chatViewModel.accept(
                    ChatAction.OnEmojiClick(
                        emojiName = resultPick.emojiName,
                        messageId = resultPick.messageId,
                        isBottomSheetClick = true
                    )
                )
            }
        }
    }

    fun progress(switch: Boolean) {
        with(binding) {
            when (switch) {
                true -> {
                    progressBar.isVisible = true
//                    progressBar.incrementProgressBy(100)
                }

                false -> {
//                    progressBar.setProgress(0, true)
                    progressBar.isGone = true
                }
            }
        }
    }

    override fun render(state: ChatState) {
        when (state) {
            ChatState.Error -> stopLoading()
            ChatState.Loading -> showLoading()
            is ChatState.Result -> showItems(state.items)
        }
    }

    override fun handleUiEffects(effect: ChatEffect) {
        when (effect) {
            is ChatEffect.SnackBar -> showError(effect.error)
            ChatEffect.Scroll -> scrollToTheEnd()
            ChatEffect.ProgressBar -> progress(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.unbind()
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
                    chatViewModel.accept(
                        ChatAction.SendMessage(
                            messageText = etMsgField.text.toString(),
                            streamName = streamName, topicName = topicName
                        )
                    )
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
        this.setStatusBarColor(R.color.color_background_primary)
    }

    private fun scrollToTheEnd() {
        binding.rvItems.smoothScrollToPosition(adapter.itemCount)
    }

    private fun showItems(items: List<ViewTyped>) {
        with(binding) {
            if (items.isEmpty()) {
                emptyState.tvEmptyState.isVisible = true
                rvItems.isGone = true
            } else {
                emptyState.tvEmptyState.isGone = true
                rvItems.isVisible = true
                adapter.items = items
            }
            stopLoading()
        }
    }

    private fun showError(error: Throwable?) {
        when (error) {
            is Errors.ReactionAlreadyExist -> Snackbar.make(
                binding.root,
                getString(R.string.reaction_already_exists),
                Snackbar.LENGTH_SHORT
            ).show()
            else -> Snackbar.make(
                binding.root,
                getString(R.string.error, error),
                Snackbar.LENGTH_SHORT
            ).show()
        }
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "Chat Problems: $error")
    }

    private fun showLoading() {
        binding.shimmerChat.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    private fun stopLoading() {
        binding.shimmerChat.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

    companion object {
        const val START_LOADING_POSITION = 5
        const val ZERO_SCROLL_POSITION = 0
        const val LAST_ID = -1
    }

}
