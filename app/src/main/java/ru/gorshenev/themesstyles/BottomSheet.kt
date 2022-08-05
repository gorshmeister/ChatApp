package ru.gorshenev.themesstyles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gorshenev.themesstyles.holderFactory.BottomSheetHolderFactory
import ru.gorshenev.themesstyles.items.EmojiUi
import ru.gorshenev.themesstyles.items.ReactionsUi
import java.io.Serializable

class BottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageId = arguments?.getInt(ARGUMENT_MSG_ID)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_emojis)
        val holderFactory = BottomSheetHolderFactory(
            onEmojiClick = { emojiCode ->
                val result = EmojiPickResult(
                    messageId = messageId ?: -1,
                    emojiCode = emojiCode
                )
                dismiss()
                setFragmentResult(PICKER_KEY, bundleOf(RESULT_EMOJI_PICK to result))
            }
        )
        val adapter = Adapter<ViewTyped>(holderFactory)

        adapter.items = setListOfEmoji()

        recyclerView.adapter = adapter
    }

    private fun setListOfEmoji(): List<ReactionsUi> {
        val list = mutableListOf<ReactionsUi>()
        Data.getAllEmojies().forEach { emoji: EmojiUi ->
            list += ReactionsUi(emoji.code)
        }
        return list
    }

    companion object {
        const val TAG = "BottomSheet"
        const val ARGUMENT_MSG_ID = "ARGUMENT_MSG_ID"
        const val PICKER_KEY = "PICKER_KEY"
        const val RESULT_EMOJI_PICK = "RESULT_EMOJI_PICK"
    }

    //todo read Parcelizable||Parcelable, differences between it
    data class EmojiPickResult(
        val messageId: Int,
        val emojiCode: Int
    ) : Serializable
}