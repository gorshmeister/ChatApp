package ru.gorshenev.themesstyles.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.gorshenev.themesstyles.MainActivity
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.SecondActivity

class ChannelsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_channel).setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

    }
}