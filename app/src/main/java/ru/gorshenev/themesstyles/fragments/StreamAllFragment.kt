package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.Adapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.StreamMapper
import ru.gorshenev.themesstyles.Utils.createStreams
import ru.gorshenev.themesstyles.Utils.setDivider
import ru.gorshenev.themesstyles.Utils.initStreamSearch
import ru.gorshenev.themesstyles.ViewTyped
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holderFactory.StreamsHolderFactory
import ru.gorshenev.themesstyles.items.TopicUi

class StreamAllFragment : Fragment(R.layout.fragment_channels_stream) {

    private var cachedItems: MutableSet<ViewTyped> = mutableSetOf()

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            adapter.items = StreamMapper.expandableStream(adapter.items, streamId)
        },
        onTopicClick = { topicId ->
            adapter.items.find { (it is TopicUi) && it.id == topicId }
                .apply {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, ChatFragment())
                        .commit()
                }
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_streams)

        adapter.items = createStreams(40)
        cachedItems += adapter.items

        recyclerView.adapter = adapter

        recyclerView.setDivider()

        parentFragmentManager.setFragmentResultListener(ChannelsFragment.STREAM_SEARCH, this) { _, result ->
            val text = result.get(ChannelsFragment.RESULT_STREAM) as String

            adapter.items = initStreamSearch(cachedItems = cachedItems, searchText = text)
        }

    }
}
