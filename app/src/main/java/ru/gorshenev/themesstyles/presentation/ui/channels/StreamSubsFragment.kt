package ru.gorshenev.themesstyles.presentation.ui.channels

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.Utils.setDivider
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.adapter.StreamsHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment

class StreamSubsFragment : Fragment(R.layout.fragment_channels_stream), StreamView {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private val presenter: StreamPresenter = StreamPresenter(this)

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId -> presenter.onStreamClick(streamId) },
        onTopicClick = { topicId -> onTopicClick(topicId) }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        presenter.loadStreams(30)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onClear()
    }


    private fun onTopicClick(topicId: Int) {
        val topic = adapter.items.find { (it is TopicUi) && it.id == topicId }
        val stream = adapter.items.find { (it is StreamUi) && it.topics.contains(topic) }
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundleOf(
            STR_NAME to (stream as StreamUi).name,
            TPC_NAME to (topic as TopicUi).name
        )

        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, chatFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initViews() {
        with(binding) {
            rvStreams.adapter = adapter
            rvStreams.setDivider()

            parentFragmentManager.setFragmentResultListener(
                STREAM_SEARCH,
                this@StreamSubsFragment
            ) { _, result ->
                val searchText = result.getString(RESULT_STREAM, "")
                presenter.searchStream(searchText?.toString().orEmpty())
            }
        }
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
    }

    override fun adapterItems(): List<ViewTyped> {
        return adapter.items
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun stopLoading() {
        Snackbar.make(binding.root, "Completed", Snackbar.LENGTH_SHORT).show()
        binding.shimmerChannels.apply {
            visibility = View.GONE
            stopShimmer()
        }
    }

    override fun showItems(items: List<ViewTyped>) {
        adapter.items = items
    }


}