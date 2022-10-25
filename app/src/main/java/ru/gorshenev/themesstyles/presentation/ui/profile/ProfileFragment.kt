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
import ru.gorshenev.themesstyles.mvi.Action
import ru.gorshenev.themesstyles.mvi.MviView
import ru.gorshenev.themesstyles.MyViewModelFactory
import ru.gorshenev.themesstyles.mvi.UiState
import ru.gorshenev.themesstyles.mvi.mvi_profile.MviViewModel
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class ProfileFragment : Fragment(R.layout.fragment_profile), MviView<Action, UiState> {
    private val binding: FragmentProfileBinding by viewBinding()

    private val profileStore = GlobalDI.INSTANSE.profileStore

    private val profileViewModel: MviViewModel<Action, UiState> by viewModels {
        MyViewModelFactory(profileStore)
    }

    private val _actions = PublishRelay.create<Action>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@ProfileFragment.setStatusBarColor(R.color.color_window_background)

        _actions.accept(Action.UploadProfile)
        profileViewModel.bind(this)
    }

    override fun render(state: UiState) {
        if (state.loading) showLoading() else stopLoading()

        if (state.error != null) showError(state.error)

        if (state.data != null) setProfile(state.data.first, state.data.second)
    }

    override val actions: Observable<Action>
        get() = _actions

    override fun onDestroyView() {
        super.onDestroyView()
        profileViewModel.unbind()
    }


    fun setProfile(name: String, avatarUrl: String) {
        with(binding) {
            tvProfileName.text = name
            Glide.with(this@ProfileFragment)
                .load(avatarUrl)
                .placeholder(R.color.shimmer_color)
                .into(ivProfileAvatar)

            ivProfileAvatar.isVisible = true
            tvProfileName.isVisible = true
            online.isVisible = true
            emptyState.tvEmptyState.isGone = true
        }
    }

    fun showEmptyState() {
        with(binding) {
            ivProfileAvatar.isGone = true
            tvProfileName.isGone = true
            online.isGone = true
            stopLoading()
            emptyState.tvEmptyState.isVisible = true
        }
    }

    fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_LONG)
            .show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "Profile problems: $error")
    }

    fun showLoading() {
        binding.shimmerProfile.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    fun stopLoading() {
        binding.shimmerProfile.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}