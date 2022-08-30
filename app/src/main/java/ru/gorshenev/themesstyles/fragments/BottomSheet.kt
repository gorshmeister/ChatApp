package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.baseRecyclerView.Adapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.ReactionsData
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.databinding.BottomSheetBinding
import ru.gorshenev.themesstyles.holderFactory.BottomSheetHolderFactory
import java.io.Serializable

class BottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet) {
    private val binding: BottomSheetBinding by viewBinding()

    //todo как?
    private lateinit var adapter: Adapter<ViewTyped>
//    private val adapter = Adapter<ViewTyped>(holderFactory)
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            getColor(requireContext(), R.color.colorPrimaryBlue)

         val messageId = arguments?.getInt(ARG_MSG_ID)
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
        adapter = Adapter(holderFactory)

        binding.rvEmojis.adapter = adapter
        loadEmojis()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
    private fun loadEmojis() {
        ReactionsData.getEmojisObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { emojis -> adapter.items = emojis }
            .apply { compositeDisposable.add(this) }
    }
//    private fun setListOfEmoji(): List<ReactionsUi> {
//        val list = mutableListOf<ReactionsUi>()
//        ReactionsData.getAllEmojies().forEach { emoji: EmojiUi ->
//            list += ReactionsUi(emoji.code)
//        }
//        return list
//    }

    companion object {
        const val TAG = "BottomSheet"
        const val ARG_MSG_ID = "ARG_MSG_ID"
        const val PICKER_KEY = "PICKER_KEY"
        const val RESULT_EMOJI_PICK = "RESULT_EMOJI_PICK"
    }

    //todo read Parcelizable||Parcelable, differences between it
    data class EmojiPickResult(
        val messageId: Int,
        val emojiCode: Int
    ) : Serializable
}