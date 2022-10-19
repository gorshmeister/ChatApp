package ru.gorshenev.themesstyles.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentProfileBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.base.MvpFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.ChannelsFragment
import ru.gorshenev.themesstyles.utils.Utils.setStatusBarColor

class ProfileFragment : MvpFragment<ProfileView, ProfilePresenter>(R.layout.fragment_profile),
    ProfileView {
    private val binding: FragmentProfileBinding by viewBinding()

    private val profilePresenter by lazy { ProfilePresenter(GlobalDI.INSTANSE.profileRepository) }

    override fun getPresenter(): ProfilePresenter = profilePresenter

    override fun getMvpView(): ProfileView = this


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@ProfileFragment.setStatusBarColor(R.color.color_window_background)
        getPresenter().uploadProfile()
    }

    override fun setProfile(name: String, avatarUrl: String) {
        with(binding) {
            tvProfileName.text = name
            Glide.with(this@ProfileFragment)
                .load(avatarUrl)
                .placeholder(R.color.shimmer_color)
                .into(ivProfileAvatar)

            tvProfileName.isVisible = true
            ivProfileAvatar.isVisible = true
            online.isVisible = true
            emptyState.tvEmptyState.isGone = true
        }
    }

    override fun showEmptyState() {
        with(binding) {
            tvProfileName.isGone = true
            ivProfileAvatar.isGone = true
            online.isGone = true
            stopLoading()
            emptyState.tvEmptyState.isVisible = true

        }
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, getString(R.string.error, error), Snackbar.LENGTH_LONG)
            .show()
        Log.d(ChannelsFragment.ERROR_LOG_TAG, "Profile problems: $error")
    }

    override fun showLoading() {
        binding.shimmerProfile.apply {
            visibility = View.VISIBLE
            showShimmer(true)
        }
    }

    override fun stopLoading() {
        binding.shimmerProfile.apply {
            visibility = View.GONE
            hideShimmer()
        }
    }

}