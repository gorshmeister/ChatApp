package ru.gorshenev.themesstyles.presentation.ui.people

import android.content.Context
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
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviView
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModel
import ru.gorshenev.themesstyles.presentation.base.mvi_core.MviViewModelFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.Adapter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.presentation.ui.people.adapter.PeopleHolderFactory
import ru.gorshenev.themesstyles.utils.Utils.appComponent
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor
import javax.inject.Inject

class PeopleFragment : Fragment(R.layout.fragment_people),
    MviView<PeopleState, PeopleEffect> {

    private val binding: FragmentPeopleBinding by viewBinding()

    private val holderFactory: HolderFactory = PeopleHolderFactory()

    private val adapter = Adapter<ViewTyped>(holderFactory)

    @Inject
    lateinit var factory: MviViewModelFactory<PeopleAction, PeopleState, PeopleEffect>

    private val peopleViewModel: MviViewModel<PeopleAction, PeopleState, PeopleEffect> by viewModels { factory }


    override fun onAttach(context: Context) {
        context.appComponent.peopleComponent().build().inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        peopleViewModel.bind(this)
        peopleViewModel.accept(PeopleAction.LoadUsers)
    }

    private fun initViews() {
        with(binding) {
            this@PeopleFragment.setStatusBarColor(R.color.color_background_primary)

            rvPeople.adapter = adapter

            usersField.etUsers.addTextChangedListener { text ->
                val currentState = peopleViewModel.state
                if (currentState is PeopleState.Result) {
                    peopleViewModel.accept(
                        PeopleAction.SearchUsers(currentState.items, text?.toString().orEmpty())
                    )
                }
            }
        }
    }

    override fun render(state: PeopleState) {
        when (state) {
            PeopleState.Error -> stopLoading()
            PeopleState.Loading -> showLoading()
            is PeopleState.Result -> showItems(state.visibleItems)
        }
    }

    override fun handleUiEffects(effect: PeopleEffect) {
        when (effect) {
            is PeopleEffect.SnackBar -> {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error, effect.error),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                Log.d(ChannelsFragment.ERROR_LOG_TAG, "People Problems: ${effect.error}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        peopleViewModel.unbind()
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