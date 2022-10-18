package ru.gorshenev.themesstyles.presentation.ui.channels

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentChannelsBinding
import ru.gorshenev.themesstyles.presentation.ui.channels.view_pager.PagerAdapter
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class ChannelsFragment : Fragment(R.layout.fragment_channels) {
    private val binding: FragmentChannelsBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        with(binding) {
            this@ChannelsFragment.setStatusBarColor(R.color.color_background_primary)

            val pagerAdapter = PagerAdapter(parentFragmentManager, lifecycle)
            fragmentViewPager.adapter = pagerAdapter

            pagerAdapter.update(
                listOf(
                    StreamFragment().apply {
                        arguments = bundleOf(STR_TYPE to StreamFragment.StreamType.SUBSCRIBED)
                    },
                    StreamFragment().apply {
                        arguments = bundleOf(STR_TYPE to StreamFragment.StreamType.ALL_STREAMS)
                    })
            )

            val tabs = listOf(getString(R.string.subscribed), getString(R.string.all_streams))

            TabLayoutMediator(tabLayout, fragmentViewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()


            searchField.etSearch.addTextChangedListener { text ->
                setFragmentResult(STREAM_SEARCH, bundleOf(RESULT_STREAM to text.toString()))
            }
        }
    }

    companion object {
        const val STREAM_SEARCH = "STREAM_SEARCH"
        const val RESULT_STREAM = "RESULT_STREAM"
        const val STR_NAME = "STR_NAME"
        const val TPC_NAME = "TPC_NAME"
        const val STR_TYPE = "STR_TYPE"
        const val ERROR_LOG_TAG = "QWE123"
    }
}