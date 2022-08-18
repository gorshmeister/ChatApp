package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.gorshenev.themesstyles.PagerAdapter
import ru.gorshenev.themesstyles.R

class ChannelsFragment : Fragment(R.layout.fragment_channels) {

    private lateinit var tabLayout: TabLayout
    private lateinit var fragmentViewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = PagerAdapter(parentFragmentManager, lifecycle)

        fragmentViewPager = view.findViewById(R.id.fragmentViewPager)
        fragmentViewPager.adapter = pagerAdapter

        pagerAdapter.update(listOf(StreamSubsFragment(), StreamAllFragment()))

        tabLayout = view.findViewById(R.id.tabLayout)
        setTabNames()


        val searchField: EditText = view.findViewById(R.id.et_search)

        searchField.doOnTextChanged { text, _, _, _ ->
            setFragmentResult(STREAM_SEARCH, bundleOf(RESULT_STREAM to text.toString()))
        }

    }

    private fun setTabNames() {
        val tabs = listOf("Subscribed", "All streams")
        TabLayoutMediator(tabLayout, fragmentViewPager) { tab, position ->
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