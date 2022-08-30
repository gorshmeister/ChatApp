package ru.gorshenev.themesstyles.fragments

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
import ru.gorshenev.themesstyles.fragments.BottomSheet.Companion.PICKER_KEY
import ru.gorshenev.themesstyles.StreamMapper.addReactionsObservable
import ru.gorshenev.themesstyles.StreamMapper.updateEmojisCounterObservable
import ru.gorshenev.themesstyles.baseRecyclerView.Adapter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentChatBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.holderFactory.ChatHolderFactory
import ru.gorshenev.themesstyles.items.DateUi
import ru.gorshenev.themesstyles.items.MessageRightUi
import kotlin.random.Random

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val binding: FragmentChatBinding by viewBinding()

    private val bottomSheet: BottomSheet = BottomSheet()
    private val compositeDisposable = CompositeDisposable()

    private val holderFactory: HolderFactory = ChatHolderFactory(
        longClick = { messageId ->
            bottomSheet.arguments = bundleOf(BottomSheet.ARG_MSG_ID to messageId)
            if (bottomSheet !in parentFragmentManager.fragments) {
                bottomSheet.show(parentFragmentManager, BottomSheet.TAG)
                true
            }
        },
        onEmojiClick = { emojiCode, messageId ->
            Observable.create<Pair<Int, Int>> { emitter ->
                emitter.onNext(emojiCode to messageId)
            }
                .flatMap { (code, id) -> updateEmojisCounterObservable(adapter.items, code, id) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updList -> adapter.items = updList }
                .apply { compositeDisposable.add(this) }
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loadMessages(Random.nextInt(24))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun loadMessages(count: Int) {
        ChatDataSource.getMessageObservable(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { messages -> adapter.items = messages }
            .apply { compositeDisposable.add(this) }
    }

    private fun initViews() {
        with(binding) {
            requireActivity().window.statusBarColor =
                getColor(requireContext(), R.color.colorPrimaryBlue)

            rvItems.adapter = adapter

            parentFragmentManager.setFragmentResultListener(PICKER_KEY, this@ChatFragment) { _, result ->
                val (messageId, emojiCode) = result.get(BottomSheet.RESULT_EMOJI_PICK) as BottomSheet.EmojiPickResult

                //todo могу я так обработать exception на то что реакция уже существует и в каком случает try/catch
//                try {
                Observable.create<Pair<Int, Int>> { emitter ->
                    emitter.onNext(messageId to emojiCode)
                }
                    .flatMap { (id, code) -> addReactionsObservable(adapter.items, id, code) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { updList -> adapter.items = updList },
                        { _ -> showToast() }
                    )
                    .apply { compositeDisposable.add(this) }
//                } catch (e: StreamMapper.ReactionAlreadyExist) {
//                    showToast()
//                }
            }

            inputField()

            sendMessage()

            setStreamAndTopicNames()
        }
    }

    private fun FragmentChatBinding.inputField() {
        etMsgField.addTextChangedListener { text ->
            if (!text.isNullOrBlank())
                btnSendMsg.setImageResource(R.drawable.icon_input_field_plane)
            else
                btnSendMsg.setImageResource(R.drawable.icon_input_field_plus)
        }
    }

    private fun FragmentChatBinding.sendMessage() {
        btnSendMsg.setOnClickListener {
            if (etMsgField.text.isNotBlank()) {
                val lastDate = (adapter.items.findLast { it is DateUi } as DateUi).text
                if (lastDate != Utils.getCurrentDate()) {
                    adapter.items += DateUi(
                        id = adapter.itemCount + 1,
                        text = Utils.getCurrentDate(),
                    )
                }

                try {
                    adapter.items += MessageRightUi(
                        id = adapter.itemCount + 1,
                        text = etMsgField.text.toString(),
                        time = Utils.getCurrentTime(),
                        emojis = emptyList()
                    )
                    if (adapter.items.size % 5 == 0) {
                        throw Errors.MessageError("Owi6ka oTnpaBku coo6weHu9I")
                    }
                } catch (e: Errors.MessageError) {
                    showError(e)
                }
            }

            rvItems.smoothScrollToPosition(adapter.itemCount)
            etMsgField.text.clear()
        }
    }

    private fun FragmentChatBinding.setStreamAndTopicNames() {
        val topicName = requireArguments().getString(TPC_NAME, "topic")
        toolbar.title = requireArguments().getString(STR_NAME, "stream")
        tvTopicName.text = "Topic: #$topicName"
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().window.statusBarColor =
                getColor(requireContext(), R.color.colorPrimaryBlack)
        }
    }

    private fun showToast() {
        Toast.makeText(context, "Реакция уже существует!", Toast.LENGTH_SHORT).show()
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, """Something wrong!
            |${error?.message}""".trimMargin(), Snackbar.LENGTH_SHORT).show()
    }

}
