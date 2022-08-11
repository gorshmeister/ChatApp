package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
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

//        view.findViewById<TextView>(R.id.tv_channel).setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container_view, ChatFragment())
//                .commit()
//        }


    }

    private fun setTabNames() {
        val tabs = listOf("Subscribed", "All streams")
        TabLayoutMediator(tabLayout, fragmentViewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }
}