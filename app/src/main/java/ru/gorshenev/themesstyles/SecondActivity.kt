package ru.gorshenev.themesstyles

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import ru.gorshenev.themesstyles.fragments.ChannelsFragment
import ru.gorshenev.themesstyles.fragments.PeopleFragment
import ru.gorshenev.themesstyles.fragments.ProfileFragment

class SecondActivity : AppCompatActivity() {
    private val listener = NavigationBarView.OnItemSelectedListener { item ->
        var selectedFragment: Fragment = ChannelsFragment()
        when (item.itemId) {
            R.id.channels -> {
                selectedFragment = ChannelsFragment()
            }
            R.id.people -> {
                selectedFragment = PeopleFragment()
            }
            R.id.profile -> {
                selectedFragment = ProfileFragment()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, selectedFragment)
            .commit()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener(listener)

    }
}