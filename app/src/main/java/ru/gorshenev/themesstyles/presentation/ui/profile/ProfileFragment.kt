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
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentProfileBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.mvi_core.*
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class ProfileFragment : Fragment(R.layout.fragment_profile),
    MviView<ProfileAction, ProfileState, UiEffects> {
    private val binding: FragmentProfileBinding by viewBinding()

    private val profileRepository = GlobalDI.INSTANSE.profileRepository

    private val profileStore: Store<ProfileAction, ProfileState, UiEffects> =
        Store(
            reducer = ProfileReducer(),
            middlewares = listOf(ProfileMiddleware(profileRepository)),
            initialState = ProfileState(isLoading = false, data = null, error = null)
        )

    private val profileViewModel: MviViewModel<ProfileAction, ProfileState, UiEffects> by viewModels {
        MviViewModelFactory(profileStore)
    }

    private val _actions = PublishRelay.create<ProfileAction>()

    companion object {
        val uiEffect = PublishRelay.create<UiEffects>()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@ProfileFragment.setStatusBarColor(R.color.color_window_background)

        profileViewModel.bind(this)
        _actions.accept(ProfileAction.UploadProfile)
    }

    override val actions: Observable<ProfileAction>
        get() = _actions

    override val effects: Observable<UiEffects>
        get() = uiEffect

    override fun render(state: ProfileState) {
        when {
            state.isLoading -> showLoading()
            state.error != null -> displayEmptyState()
            state.data != null -> displayDownloadedProfile(state.data)
        }
    }

    override fun handleUiEffects(effect: UiEffects) {
        when (effect) {
            is UiEffects.SnackBar -> displayError(effect.error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileViewModel.unbind()
    }


    private fun displayDownloadedProfile(profile: Profile) {
        with(binding) {
            Glide.with(this@ProfileFragment)
                .load(profile.avatar)
                .placeholder(R.color.shimmer_color)
                .into(ivProfileAvatar)
            tvProfileName.text = profile.name

            ivProfileAvatar.isVisible = true
            tvProfileName.isVisible = true
            online.isVisible = true
            stopLoading()
            emptyState.tvEmptyState.isGone = true
        }
    }

    private fun displayEmptyState() {
        with(binding) {
            ivProfileAvatar.isGone = true
            tvProfileName.isGone = true
            online.isGone = true
            stopLoading()
            emptyState.tvEmptyState.isVisible = true
        }
    }

    private fun displayError(error: Throwable?) {
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