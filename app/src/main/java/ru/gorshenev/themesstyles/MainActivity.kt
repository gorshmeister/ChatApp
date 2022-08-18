package ru.gorshenev.themesstyles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import ru.gorshenev.themesstyles.fragments.ChannelsFragment
import ru.gorshenev.themesstyles.fragments.ChatFragment
import ru.gorshenev.themesstyles.fragments.PeopleFragment
import ru.gorshenev.themesstyles.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val listener = NavigationBarView.OnItemSelectedListener { item ->
        val selectedFragment: Fragment = when (item.itemId) {
            R.id.channels -> {
                ChannelsFragment()
            }
            R.id.people -> {
                PeopleFragment()
            }
            R.id.profile -> {
                ProfileFragment()
            }
            else -> throw Error("Unknown fragment!!@!@!@!")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, selectedFragment)
            .commit()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            //todo при открытии BottomSheet неправильно работает
//            bottomNavigationView.isVisible = fragment !is ChatFragment
            bottomNavigationView.isVisible = when (fragment) {
                is ChatFragment, is BottomSheet -> false
                else -> true
            }

            val color = when (fragment) {
                is ChatFragment, is BottomSheet -> getColor(R.color.colorPrimaryBlue)
                is ProfileFragment -> getColor(R.color.colorBackground)
                else -> getColor(R.color.colorPrimaryBlack)
            }
            window.statusBarColor = color
        }

        bottomNavigationView.setOnItemSelectedListener(listener)
    }
}