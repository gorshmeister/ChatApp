package ru.gorshenev.themesstyles.presentation.ui.channels

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.data.repositories.StreamDataSource
import ru.gorshenev.themesstyles.data.mappers.StreamMapper
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.data.Utils.setDivider
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.adapter.StreamsHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment
import java.util.concurrent.TimeUnit

class StreamSubsFragment : Fragment(R.layout.fragment_channels_stream) {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private val compositeDisposable = CompositeDisposable()
    private val textSubject: PublishSubject<String> = PublishSubject.create()

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            Observable.just(streamId)
                .flatMapSingle { id -> StreamMapper.expandableStream(adapter.items, id) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updItems -> adapter.items = updItems },
                    { error -> showError(error) })
                .apply { compositeDisposable.add(this) }
        },
        onTopicClick = { topicId ->
            onTopicClick(topicId)
        }
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)
    private var cachedItems: List<ViewTyped> = listOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        loadStreams(30)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
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

    private fun loadStreams(count: Int) {
        StreamDataSource.getStreams(count)
            .subscribeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext { binding.shimmerChannels.startShimmer() }
            .subscribe(
                { streams ->
                    adapter.items = streams
                    cachedItems = streams
                },
                { error -> showError(error) },
                {
                    showSuccess()
                    shimmerStop()
                })
            .apply { compositeDisposable.add(this) }
    }

    private fun initViews() {
        with(binding) {
            rvStreams.adapter = adapter
            rvStreams.setDivider()

            parentFragmentManager.setFragmentResultListener(STREAM_SEARCH, this@StreamSubsFragment) { _, result ->
                val searchText = result.getString(RESULT_STREAM, "")
                val queue = searchText?.toString().orEmpty()
                textSubject.onNext(queue)

                textSubject
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMapSingle { text -> Utils.initStreamSearch(cachedItems, text) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { filteredItems -> adapter.items = filteredItems },
                        { error -> showError(error) })
                    .apply { compositeDisposable.add(this) }
            }
        }
    }

    private fun shimmerStop() {
        binding.shimmerChannels.apply {
            visibility = View.GONE
            stopShimmer()
        }
    }

    private fun showSuccess() {
        Snackbar.make(binding.root, "Completed", Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
    }

}