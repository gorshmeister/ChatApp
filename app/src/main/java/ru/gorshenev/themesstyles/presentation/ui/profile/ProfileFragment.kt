package ru.gorshenev.themesstyles.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentProfileBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.mvi_core.*
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile),
    MviView<ProfileState, UiEffects> {
    private val binding: FragmentProfileBinding by viewBinding()

    private val profileViewModel: MviViewModel<ProfileAction, ProfileState, UiEffects> by viewModels {
        val profileStore: Store<ProfileAction, ProfileState, UiEffects> =
            Store(
                reducer = ProfileReducer(),
                middlewares = listOf(ProfileMiddleware(GlobalDI.INSTANSE.profileRepository)),
                initialState = ProfileState.Loading
            )
        MviViewModelFactory(profileStore)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@ProfileFragment.setStatusBarColor(R.color.color_window_background)

        profileViewModel.bind(this)
        profileViewModel.accept(ProfileAction.UploadProfile)
    }

    override fun render(state: ProfileState) {
        when (state) {
            ProfileState.Error -> showEmptyState()
            ProfileState.Loading -> showLoading()
            is ProfileState.Result -> showProfile(
                profileName = state.profileName,
                avatarUrl = state.avatarUrl
            )
        }
    }

    override fun handleUiEffects(effect: UiEffects) {
        when (effect) {
            is UiEffects.SnackBar -> showError(effect.error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileViewModel.unbind()
    }


    private fun showProfile(profileName: String, avatarUrl: String) {
        with(binding) {
            Glide.with(this@ProfileFragment)
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

    private fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_LONG)
            .show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "Profile problems: $error")
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