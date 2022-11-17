package ru.gorshenev.themesstyles.presentation.ui.profile.coroutines

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentProfileBinding
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.utils.Utils.appComponent
import javax.inject.Inject

class ProfileCoroutinesFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var factory: ViewModelFactory<ProfileViewModel>

    private val viewModel: ProfileViewModel by viewModels { factory }

    private val binding: FragmentProfileBinding by viewBinding()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.profileComponent().build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.states.onEach(::render).launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.effects.onEach(::handleUiEffects).launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.accept(ProfileAction.LoadProfile)
    }

    private fun render(state: ProfileState) {
        when (state) {
            ProfileState.Error -> showEmptyState()
            ProfileState.Loading -> showLoading()
            is ProfileState.Result -> showProfile(
                profileName = state.profileName,
                avatarUrl = state.avatarUrl
            )
        }
    }

    private fun handleUiEffects(effect: ProfileEffect) {
        when (effect) {
            is ProfileEffect.SnackBar -> {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error, effect.error),
                    Snackbar.LENGTH_LONG
                )
                    .show()
                Log.d(ChannelsFragment.ERROR_LOG_TAG, "Profile problems: ${effect.error}")
            }
        }
    }

    private fun showProfile(profileName: String, avatarUrl: String) {
        with(binding) {
            Glide.with(requireContext())
                .load(avatarUrl)
                .placeholder(R.color.shimmer_color)
                .into(ivProfileAvatar)
            tvProfileName.text = profileName

            ivProfileAvatar.isVisible = true
            tvProfileName.isVisible = true
            online.isVisible = true
            stopLoading()
            emptyState.tvEmptyState.isGone = true
        }
    }

    private fun showEmptyState() {
        with(binding) {
            ivProfileAvatar.isGone = true
            tvProfileName.isGone = true
            online.isGone = true
            stopLoading()
            emptyState.tvEmptyState.isVisible = true
        }
    }

    private fun showLoading() {
        binding.shimmerProfile.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    private fun stopLoading() {
        binding.shimmerProfile.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}