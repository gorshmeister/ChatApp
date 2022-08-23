package ru.gorshenev.themesstyles.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.Adapter
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.createPeople
import ru.gorshenev.themesstyles.Utils.initUserSearch
import ru.gorshenev.themesstyles.ViewTyped
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.holderFactory.PeopleHolderFactory
import ru.gorshenev.themesstyles.items.PeopleUi

class PeopleFragment : Fragment(R.layout.fragment_people) {
    private val binding: FragmentPeopleBinding by viewBinding()

    private val cachedItems: MutableSet<ViewTyped> = mutableSetOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimaryBlack)

        val holderFactory: HolderFactory = PeopleHolderFactory()

        val adapter = Adapter<ViewTyped>(holderFactory)

        adapter.items = createPeople(18)
        cachedItems += adapter.items

        binding.rvPeople.adapter = adapter


        binding.usersField.etUsers.doOnTextChanged { text, _, _, _ ->
            adapter.items = initUserSearch(cachedItems, text.toString())
        }
    }
}