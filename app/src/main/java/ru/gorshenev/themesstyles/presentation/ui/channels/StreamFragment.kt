package ru.gorshenev.themesstyles.presentation.ui.channels

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_TYPE
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.adapter.StreamsHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment
import ru.gorshenev.themesstyles.utils.Utils.setDivider

class StreamFragment : Fragment(R.layout.fragment_channels_stream), StreamView {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private val db: AppDataBase by lazy { AppDataBase.getDataBase(requireContext()) }

    private val streamRepository: StreamRepository by lazy { StreamRepository(db.streamDao()) }

    private val presenter: StreamPresenter = StreamPresenter(this)

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId -> presenter.onStreamClick(streamId) },
        onTopicClick = { topicId -> presenter.onTopicClick(topicId) }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)

//    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
//        if (isAdded && context != null) {
//            operation(requireContext())
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        val streamType = arguments?.get(STR_TYPE) as StreamType
        presenter.loadStreams(streamType)
//        presenter.loadStreamsFromDataBase(streamType)
//        presenter.loadStreamsFromApi(streamType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onClear()
    }


    private fun initViews() {
        with(binding) {
            rvStreams.adapter = adapter
            rvStreams.setDivider()

            parentFragmentManager.setFragmentResultListener(
                STREAM_SEARCH,
                this@StreamFragment
            ) { _, result ->
                val searchText = result.getString(RESULT_STREAM, "")
                presenter.searchStream(searchText?.toString().orEmpty())
            }
        }
    }

    override fun goToChat(topic: TopicUi, stream: StreamUi) {
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundleOf(STR_NAME to stream.name, TPC_NAME to topic.name)

        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, chatFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
        Log.d("qweqwe", "STREAM PROBLEM: $error")
    }

    override fun showLoading() {
        binding.shimmerChannels.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    override fun stopLoading() {
        binding.shimmerChannels.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

    override fun getDataBase(): AppDataBase {
        return db
    }

    override fun repository(): StreamRepository {
        return streamRepository
    }

    override fun showItems(items: List<ViewTyped>) {
        adapter.items = items
    }

    enum class StreamType {
        SUBSCRIBED,
        ALL_STREAMS
    }

}