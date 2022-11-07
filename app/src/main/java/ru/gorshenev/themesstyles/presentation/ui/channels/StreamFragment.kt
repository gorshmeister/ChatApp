package ru.gorshenev.themesstyles.presentation.ui.channels

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentChannelsStreamBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviView
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModel
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModelFactory
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Store
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.RESULT_STREAM
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STREAM_SEARCH
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.STR_TYPE
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment.Companion.TPC_NAME
import ru.gorshenev.themesstyles.presentation.ui.channels.adapter.StreamsHolderFactory
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.ExpandStreamMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.OpenChatMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.SearchMiddleware
import ru.gorshenev.themesstyles.presentation.ui.channels.middleware.UploadMiddleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment
import ru.gorshenev.themesstyles.utils.Utils.setDivider

class StreamFragment : Fragment(R.layout.fragment_channels_stream),
    MviView<StreamState, StreamEffect> {
    private val binding: FragmentChannelsStreamBinding by viewBinding()

    private val streamType by lazy { arguments?.get(STR_TYPE) as StreamType }

    private val streamViewModel: MviViewModel<StreamAction, StreamState, StreamEffect> by viewModels {
        val streamStore: Store<StreamAction, StreamState, StreamEffect> =
            Store(
                reducer = StreamReducer(),
                middlewares = listOf(
                    UploadMiddleware(GlobalDI.INSTANSE.streamRepository),
                    SearchMiddleware(),
                    OpenChatMiddleware(),
                    ExpandStreamMiddleware()
                ),
                initialState = StreamState.Loading
            )
        MviViewModelFactory(streamStore)
    }

    private val holderFactory: HolderFactory = StreamsHolderFactory(
        onStreamClick = { streamId ->
            streamViewModel.state.ifResult {
                streamViewModel.accept(StreamAction.OnStreamClick(streamId, it.visibleItems))
            }
        },
        onTopicClick = { topicId ->
            streamViewModel.state.ifResult {
                streamViewModel.accept(StreamAction.OnTopicClick(topicId, it.visibleItems))
            }
        }
    )

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()

        streamViewModel.bind(this)
        streamViewModel.accept(StreamAction.UploadStreams(streamType))
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
                streamViewModel.state.ifResult {
                    streamViewModel.accept(
                        StreamAction.SearchStream(
                            items = it.items,
                            query = searchText?.toString().orEmpty()
                        )
                    )
                }
            }
        }
    }

    override fun render(state: StreamState) {
        when (state) {
            StreamState.Error -> stopLoading()
            StreamState.Loading -> showLoading()
            is StreamState.Result -> showItems(state.visibleItems)
        }
    }

    override fun handleUiEffects(effect: StreamEffect) {
        when (effect) {
            is StreamEffect.SnackBar -> showError(effect.error)
            is StreamEffect.OpenChat -> goToChat(effect.topicName, effect.streamName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        streamViewModel.unbind()
    }


    private fun goToChat(topicName: String, streamName: String) {
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundleOf(STR_NAME to streamName, TPC_NAME to topicName)

        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, chatFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showItems(items: List<ViewTyped>) {
        stopLoading()
        with(binding) {
            if (items.isEmpty()) {
                emptyState.tvEmptyState.isVisible = true
                rvStreams.isGone = true
            } else {
                emptyState.tvEmptyState.isGone = true
                rvStreams.isVisible = true
                adapter.items = items
            }
        }
    }

    private fun showLoading() {
        binding.shimmerChannels.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    private fun stopLoading() {
        binding.shimmerChannels.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_SHORT).show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "Stream Problems: $error")
    }

    private fun StreamState.ifResult(action: (state: StreamState.Result) -> Unit) {
        (this as? StreamState.Result)?.let(action)
    }


    enum class StreamType {
        SUBSCRIBED,
        ALL_STREAMS
    }


}