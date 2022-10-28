package ru.gorshenev.themesstyles.presentation.ui.people

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentPeopleBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.*
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class PeopleFragment : Fragment(R.layout.fragment_people),
    MviView<PeopleState, UiEffects> {

    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()

    private val adapter = Adapter<ViewTyped>(holderFactory)

    private var cachedItems = emptyList<ViewTyped>()

    private val peopleViewModel: MviViewModel<PeopleAction, PeopleState, UiEffects> by viewModels {
        val peopleStore: Store<PeopleAction, PeopleState, UiEffects> =
            Store(
                reducer = PeopleReducer(),
                middlewares = listOf(
                    PeopleUploadMiddleware(GlobalDI.INSTANSE.peopleRepository),
                    PeopleSearchMiddleware()
                ),
                initialState = PeopleState.Loading
            )
        MviViewModelFactory(peopleStore)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        peopleViewModel.bind(this)
        peopleViewModel.accept(PeopleAction.UploadUsers)
    }


    private fun initViews() {
        with(binding) {
            this@PeopleFragment.setStatusBarColor(R.color.color_background_primary)

            rvPeople.adapter = adapter

            usersField.etUsers.addTextChangedListener { text ->
                peopleViewModel.accept(
                    PeopleAction.SearchUsers(cachedItems, text?.toString().orEmpty())
                )
            }
        }
    }

    override fun render(state: PeopleState) {
        when (state) {
            PeopleState.Error -> stopLoading()
            PeopleState.Loading -> showLoading()
            is PeopleState.Result -> showItems(state.items)
            is PeopleState.ResultWithCache -> {
                cachedItems = state.items
                showItems(state.items)
            }
        }
    }

    override fun handleUiEffects(effect: UiEffects) {
        when (effect) {
            is UiEffects.SnackBar -> showError(effect.error)
        }
    }


    private fun showItems(items: List<ViewTyped>) {
        stopLoading()
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

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_LONG)
            .show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "People Problems: $error")
    }

    private fun showLoading() {
        binding.shimmerPeople.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    private fun stopLoading() {
        binding.shimmerPeople.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}