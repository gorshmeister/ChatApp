package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.Adapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.StreamMapper
import ru.gorshenev.themesstyles.Utils.createStreams
import ru.gorshenev.themesstyles.Utils.initStreamSearch
import ru.gorshenev.themesstyles.Utils.setDivider
import ru.gorshenev.themesstyles.ViewTyped
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.holderFactory.StreamsHolderFactory
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.items.TopicUi

class StreamSubsFragment : Fragment(R.layout.fragment_channels_stream) {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private var cachedItems: MutableSet<ViewTyped> = mutableSetOf()

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            adapter.items = StreamMapper.expandableStream(adapter.items, streamId)
        },
        onTopicClick = { topicId ->
            adapter.items.find { (it is TopicUi) && it.id == topicId }
                .apply {

                    val stream = adapter.items.find { (it is StreamUi) && it.topics.contains(this) }
                    val chatFragment = ChatFragment()
                    chatFragment.arguments = bundleOf(
                        STR_NAME to (stream as StreamUi).name,
                        TPC_NAME to (this as TopicUi).name
                    )

                    parentFragmentManager.beginTransaction()
                        .add(R.id.fragment_container_view, chatFragment)
                        .addToBackStack(null)
                        .commit()
                }
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter.items = createStreams(50)
        cachedItems += adapter.items

        with(binding) {
            rvStreams.setDivider()

            rvStreams.adapter = adapter
        }

        parentFragmentManager.setFragmentResultListener(STREAM_SEARCH, this) { _, result ->
            val searchText = result.getString(RESULT_STREAM,"")
            adapter.items = initStreamSearch(cachedItems = cachedItems, searchText = searchText)
        }
    }

}