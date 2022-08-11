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

class StreamSubsFragment : Fragment(R.layout.fragment_channels_stream) {

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            val toDelete = mutableListOf<TopicUi>()
            adapter.items = adapter.items.flatMap {
                when (it) {
                    is StreamUi -> when {
                        it.id == streamId && !it.isExpanded -> {
                            it.isExpanded = true
                            listOf(it) + it.topics
                        }
                        it.id == streamId && it.isExpanded -> {
                            it.isExpanded = false
                            toDelete.addAll(it.topics)
                            listOf(it)
                        }
                        else -> listOf(it)
                    }
                    is TopicUi -> when (it) {
                        in toDelete -> {
                            adapter.items - it
                            toDelete - it
                            listOf()
                        }
                        else -> {
                            listOf(it)
                        }
                    }

                    else -> listOf(it)
                }
            }
        },
        onTopicClick = { topicId ->
            adapter.items = adapter.items.map { item ->
                when (item) {
                    is TopicUi -> when {
                        item.id == topicId -> {
                            //todo где parent, а где child
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_streams)

        var id = adapter.items.size
        adapter.items = listOf(
            StreamUi(
                id = ++id,
                name = "#general",
                topics = listOf(
                    TopicUi(++id, "test"),
                    TopicUi(++id, "test"),
                ),
            ),
            StreamUi(
                id = ++id,
                name = "#mayor",
                topics = listOf(
                    TopicUi(++id, "test"),
                    TopicUi(++id, "test"),
                )
            ),
            StreamUi(
                id = ++id,
                name = "#serjant",
                topics = listOf(
                    TopicUi(++id, "test"),
                    TopicUi(++id, "test"),
                )
            ),
            StreamUi(
                id = ++id,
                name = "#praporshik",
                topics = listOf(
                    TopicUi(++id, "test"),
                    TopicUi(++id, "test"),
                )
            ),
        )

        recyclerView.setDivider()

        recyclerView.adapter = adapter
    }
}