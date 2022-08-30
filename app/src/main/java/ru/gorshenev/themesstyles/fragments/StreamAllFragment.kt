package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.*
import ru.gorshenev.themesstyles.Utils.setDivider
import ru.gorshenev.themesstyles.baseRecyclerView.Adapter
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.fragments.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.holderFactory.StreamsHolderFactory
import ru.gorshenev.themesstyles.items.TopicUi
import java.util.concurrent.TimeUnit

class StreamAllFragment : Fragment(R.layout.fragment_channels_stream) {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            //todo
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
    private var cachedItems: List<ViewTyped> = listOf()
    private val compositeDisposable = CompositeDisposable()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        loadStreams(30)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun loadStreams(count: Int) {
        StreamDataSource.getStreamsObservable(count)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streams ->
                    adapter.items = streams
                    cachedItems = streams
                },
                { error -> showError(error) },
                { Snackbar.make(binding.root, "Completed", Snackbar.LENGTH_SHORT).show() }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun initViews() {
        with(binding) {
            rvStreams.adapter = adapter
            rvStreams.setDivider()

            parentFragmentManager.setFragmentResultListener(
                STREAM_SEARCH,
                this@StreamAllFragment
            ) { _, result ->
                val searchText = result.getString(RESULT_STREAM, "")

                Observable.create { emitter ->
                    emitter.onNext(searchText)
                }
                    .filter { text -> text.isNotEmpty() }
                    .distinctUntilChanged()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .switchMap { text -> Utils.initStreamSearchObservable(cachedItems, text) }
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
