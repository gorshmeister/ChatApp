package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.gorshenev.themesstyles.PagerAdapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentChannelsBinding

class ChannelsFragment : Fragment(R.layout.fragment_channels) {
    private val binding: FragmentChannelsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            getColor(requireContext(), R.color.colorPrimaryBlack)

        val pagerAdapter = PagerAdapter(parentFragmentManager, lifecycle)

        binding.fragmentViewPager.adapter = pagerAdapter

        pagerAdapter.update(listOf(StreamSubsFragment(), StreamAllFragment()))

        setTabNames()

        binding.searchField.etSearch.doOnTextChanged { text, _, _, _ ->
            setFragmentResult(STREAM_SEARCH, bundleOf(RESULT_STREAM to text.toString()))
        }

    }

    private fun setTabNames() {
        val tabs = listOf("Subscribed", "All streams")
        TabLayoutMediator(binding.tabLayout, binding.fragmentViewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    companion object {
        const val STREAM_SEARCH = "STREAM_SEARCH"
        const val RESULT_STREAM = "RESULT_STREAM"
        const val STR_NAME = "STR_NAME"
        const val TPC_NAME = "TPC_NAME"
    }
}