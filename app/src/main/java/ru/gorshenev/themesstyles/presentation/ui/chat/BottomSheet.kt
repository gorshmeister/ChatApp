package ru.gorshenev.themesstyles.presentation.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.repositories.chat.Reactions
import ru.gorshenev.themesstyles.databinding.BottomSheetBinding
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.chat.adapter.BottomSheetHolderFactory
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor
import java.io.Serializable

class BottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet) {
    private val binding: BottomSheetBinding by viewBinding()

    private val compositeDisposable = CompositeDisposable()

    private val messageId: Int
        get() = arguments?.getInt(ARG_MSG_ID) ?: -1

    private val holderFactory = BottomSheetHolderFactory(
        onEmojiClick = { emojiName, emojiCode ->
            val result = EmojiPickResult(
                messageId = messageId,
                emojiName = emojiName,
                emojiCode = emojiCode
            )
            setFragmentResult(PICKER_KEY, bundleOf(RESULT_EMOJI_PICK to result))
            dismiss()
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setStatusBarColor(R.color.color_primary)

        binding.rvEmojis.adapter = adapter
        loadEmojis()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun loadEmojis() {
        Reactions.getReactions()
            .map { list -> list.filter { it.category == "Smileys & Emotion" } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { reactions -> adapter.items = reactions },
                { err ->
                    Log.d(
                        ChannelsFragment.ERROR_LOG_TAG,
                        getString(R.string.log_error, "Bottom_sheet Problems: ", err)
                    )
                }
            )
            .apply { compositeDisposable.add(this) }
    }

    companion object {
        const val TAG = "BottomSheet"
        const val ARG_MSG_ID = "ARG_MSG_ID"
        const val PICKER_KEY = "PICKER_KEY"
        const val RESULT_EMOJI_PICK = "RESULT_EMOJI_PICK"
    }

    //todo read Parcelizable||Parcelable, differences between it
    data class EmojiPickResult(
        val messageId: Int,
        val emojiName: String,
        val emojiCode: String
    ) : Serializable
}