package ru.gorshenev.themesstyles.presentation.ui.people

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.base.MvpFragment
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class PeopleFragment : MvpFragment<PeopleView, PeoplePresenter>(R.layout.fragment_people),
    PeopleView {

    private val binding: FragmentPeopleBinding by viewBinding()

    private val peoplePresenter by lazy { PeoplePresenter(GlobalDI.INSTANSE.peopleRepository) }

    override fun getPresenter(): PeoplePresenter = peoplePresenter

    override fun getMvpView(): PeopleView = this

    private val holderFactory: HolderFactory = PeopleHolderFactory()

    private val adapter = Adapter<ViewTyped>(holderFactory)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        getPresenter().loadPeople()
    }

    private fun initViews() {
        with(binding) {
            this@PeopleFragment.setStatusBarColor(R.color.color_background_primary)

            rvPeople.adapter = adapter

            usersField.etUsers.addTextChangedListener { text ->
                getPresenter().searchPeople(text?.toString().orEmpty())
            }
        }
    }

    override fun showItems(items: List<ViewTyped>) {
        with(binding) {
            if (items.isEmpty()) {
                emptyState.tvEmptyState.isVisible = true
                rvPeople.isGone = true
            } else {
                emptyState.tvEmptyState.isGone = true
                rvPeople.isVisible = true
                adapter.items = items
            }
        }

    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_LONG)
            .show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "People Problems: $error")
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