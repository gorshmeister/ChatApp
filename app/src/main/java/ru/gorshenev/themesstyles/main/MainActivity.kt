package ru.gorshenev.themesstyles.main

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ActivityMainBinding
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.people.PeopleFragment
import ru.gorshenev.themesstyles.presentation.ui.profile.coroutines.ProfileCoroutinesFragment
import ru.gorshenev.themesstyles.presentation.ui.profile.rx.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
            initViews()
//        registerNetworkCallback()
    }


    private fun initViews() {
        with(binding) {
            bottomNavigation.setOnItemSelectedListener { item ->
                val selectedFragment: Fragment = when (item.itemId) {
                    R.id.channels -> ChannelsFragment()
                    R.id.people -> PeopleFragment()
                    R.id.profile -> ProfileCoroutinesFragment()
                    else -> throw Error(getString(R.string.unknown_fragment))
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, selectedFragment)
                    .commit()
                true
            }

            supportFragmentManager.addOnBackStackChangedListener {
                bottomNavigation.isVisible =
                    supportFragmentManager.backStackEntryCount == BACK_STACK_IS_EMPTY
            }
        }
    }

    companion object {
        const val BACK_STACK_IS_EMPTY = 0
    }

    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Toast.makeText(this@MainActivity, "Default Network Available", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onLost(network: Network) {
                Toast.makeText(
                    this@MainActivity,
                    "Default Network NOT Available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        )
    }

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return network != null && capabilities != null
    }

}
