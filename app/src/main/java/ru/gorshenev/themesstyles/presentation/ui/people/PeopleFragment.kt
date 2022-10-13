package ru.gorshenev.themesstyles.presentation.ui.people

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.MvpFragment
import ru.gorshenev.themesstyles.presentation.base_recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import ru.gorshenev.themesstyles.utils.Utils

class PeopleFragment : MvpFragment<PeopleView, PeoplePresenter>(R.layout.fragment_people),
    PeopleView {

    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()

    private val adapter = Adapter<ViewTyped>(holderFactory)

    override fun getPresenter(): PeoplePresenter = GlobalDI.INSTANSE.peoplePresenter

    override fun getMvpView(): PeopleView = this


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        getPresenter().loadPeople()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getPresenter().onClear()
    }


    private fun initViews() {
        with(binding) {
            Utils.setStatusBarColor(this@PeopleFragment, R.color.color_background_primary)

            rvPeople.adapter = adapter

            usersField.etUsers.addTextChangedListener { text ->
                getPresenter().searchPeople(text?.toString().orEmpty())
            }
        }
    }

    override fun showItems(items: List<ViewTyped>) {
        adapter.items = items
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_LONG).show()
        Log.d("qweqwe", "PEOPLE PROBLEM: $error")
    }

    override fun showLoading() {
        binding.shimmerPeople.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    override fun stopLoading() {
        binding.shimmerPeople.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}