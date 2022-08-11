package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.Adapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.setDivider
import ru.gorshenev.themesstyles.ViewTyped
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holderFactory.StreamsHolderFactory
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.items.TopicUi

class StreamAllFragment : Fragment(R.layout.fragment_channels_stream) {

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            adapter.items = adapter.items.flatMap {
                val stream = (it as StreamUi)
                when {
                    stream.id == streamId && !stream.isExpanded -> {
                        stream.isExpanded = true
                        listOf(stream) + stream.topics
                    }
                    else -> listOf(stream)
                }
            }
        },
        onTopicClick = { topicId ->
            adapter.items = adapter.items.map { item ->
                when (item) {
                    is TopicUi -> when {
                        item.id == topicId -> {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, ChatFragment())
                                .commit()
                            item
                        }

                        else -> item
                    }
                    else -> item
                }

            }
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_streams)

        adapter.items = listOf(
            StreamUi(0, "#ABC", emptyList()),
            StreamUi(0, "#DEF", emptyList()),
            StreamUi(0, "#GHI", emptyList()),
            StreamUi(0, "#JKL", emptyList()),
            StreamUi(0, "#MNO", emptyList()),
            StreamUi(0, "#PQR", emptyList()),
            StreamUi(0, "#STU", emptyList()),
            StreamUi(0, "#VWX", emptyList()),
            StreamUi(0, "#YZ", emptyList()),
        )

        recyclerView.setDivider()

        recyclerView.adapter = adapter
    }
}
