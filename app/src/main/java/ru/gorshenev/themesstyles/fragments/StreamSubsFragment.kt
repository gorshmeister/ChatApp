package ru.gorshenev.themesstyles.fragments

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
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.StreamDataSource
import ru.gorshenev.themesstyles.StreamMapper.expandableStreamObservable
import ru.gorshenev.themesstyles.Utils.initStreamSearchObservable
import ru.gorshenev.themesstyles.Utils.setDivider
import ru.gorshenev.themesstyles.baseRecyclerView.Adapter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.holderFactory.StreamsHolderFactory
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.items.TopicUi
import java.util.concurrent.TimeUnit

class StreamSubsFragment : Fragment(R.layout.fragment_channels_stream) {
    private val binding: FragmentChannelsStreamBinding by viewBinding()
    private val compositeDisposable = CompositeDisposable()

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            Observable.create<Int> { emitter ->
                emitter.onNext(streamId)
            }
                .flatMap { id -> expandableStreamObservable(adapter.items, id) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updItems -> adapter.items = updItems }
                .apply { compositeDisposable.add(this) }
        },
        onTopicClick = { topicId ->
//  todo почемму через cachedItems приходит null и нужен ли тут Обсервабл
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
    )
    private val adapter = Adapter<ViewTyped>(holderFactory)
    private var cachedItems: List<ViewTyped> = listOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        loadStreams(30)
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerChannels.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        binding.shimmerChannels.stopShimmer()
    }

    private fun loadStreams(count: Int) {
        StreamDataSource.getStreamsObservable(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streams ->
                    adapter.items = streams
                    cachedItems = streams
                },
                { error -> showError(error) },
                {
                    Snackbar.make(binding.root, "Completed", Snackbar.LENGTH_SHORT).show()
                    binding.shimmerChannels.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                }
            )
            .apply { compositeDisposable.add(this) }
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

                Observable.create { emitter ->
                    emitter.onNext(searchText)
                }
//                    .filter { text -> text.isNotEmpty() }
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMap { text -> initStreamSearchObservable(cachedItems, text) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { filteredItems -> adapter.items = filteredItems }
                    .apply { compositeDisposable.add(this) }
            }
        }
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_SHORT).show()
    }

}