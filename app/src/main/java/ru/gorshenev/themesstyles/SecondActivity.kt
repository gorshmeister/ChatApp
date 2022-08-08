package ru.gorshenev.themesstyles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import ru.gorshenev.themesstyles.fragments.ChannelsFragment
import ru.gorshenev.themesstyles.fragments.PeopleFragment
import ru.gorshenev.themesstyles.fragments.ProfileFragment

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        findViewById<RelativeLayout>(R.id.btn_channels).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ChannelsFragment(), )
                .commit()
        }
        findViewById<RelativeLayout>(R.id.btn_people).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, PeopleFragment(), )
                .commit()
        }
        findViewById<RelativeLayout>(R.id.btn_profile).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, ProfileFragment(), )
                .commit()
        }

    }
}