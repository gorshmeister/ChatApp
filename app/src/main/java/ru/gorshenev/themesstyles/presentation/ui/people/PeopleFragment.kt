package ru.gorshenev.themesstyles.presentation.ui.people

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import kotlin.random.Random

class PeopleFragment : Fragment(R.layout.fragment_people), PeopleView {

    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()

    private val adapter = Adapter<ViewTyped>(holderFactory)

    private val presenter = PeoplePresenter(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        presenter.loadPeople(30)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onClear()
    }


    private fun initViews() {
        with(binding) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryBlack)

            rvPeople.adapter = adapter
            usersField.ivSearch.setOnClickListener {
                adapter.items = emptyList()
                presenter.loadPeople(Random.nextInt(50))
            }

            usersField.etUsers.addTextChangedListener { text ->
                presenter.searchPeople(text?.toString().orEmpty())
            }
        }
    }

    override fun showItems(items: List<ViewTyped>) {
        adapter.items = items
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_LONG).show()
    }

    override fun showLoading() {
        binding.shimmerPeople.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    override fun stopLoading() {
        Snackbar.make(binding.root, "Completed!", Snackbar.LENGTH_SHORT).show()
        binding.shimmerPeople.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}