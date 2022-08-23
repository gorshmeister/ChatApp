package ru.gorshenev.themesstyles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import ru.gorshenev.themesstyles.databinding.ActivityMainBinding
import ru.gorshenev.themesstyles.fragments.ChannelsFragment
import ru.gorshenev.themesstyles.fragments.PeopleFragment
import ru.gorshenev.themesstyles.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::bind)

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

        supportFragmentManager.addOnBackStackChangedListener {
            binding.bottomNavigation.isVisible = supportFragmentManager.backStackEntryCount == 0
        }

        binding.bottomNavigation.setOnItemSelectedListener(listener)
    }
}