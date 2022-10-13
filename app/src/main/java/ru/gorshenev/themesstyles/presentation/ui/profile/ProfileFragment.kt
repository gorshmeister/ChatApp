package ru.gorshenev.themesstyles.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.FragmentProfileBinding
import ru.gorshenev.themesstyles.di.GlobalDI
import ru.gorshenev.themesstyles.presentation.MvpFragment
import ru.gorshenev.themesstyles.utils.Utils

class ProfileFragment : MvpFragment<ProfileView,ProfilePresenter>(R.layout.fragment_profile), ProfileView {
    private val binding: FragmentProfileBinding by viewBinding()

    override fun getPresenter(): ProfilePresenter = GlobalDI.INSTANSE.profilePresenter

    override fun getMvpView(): ProfileView = this


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setStatusBarColor(this@ProfileFragment, R.color.color_window_background)

        getPresenter().uploadProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getPresenter().onClear()
    }

    override fun showError(error: Throwable?) {
        Snackbar.make(binding.root, "Something wrong! $error", Snackbar.LENGTH_LONG).show()
        Log.d("qweqwe", "PROFILE PROBLEM: $error")
    }

    override fun setProfile(name: String, avatarUrl: String) {
        with(binding) {
            tvProfileName.text = name
            online.isVisible = true
            Glide.with(this@ProfileFragment).load(avatarUrl)
                .placeholder(R.color.shimmer_color)
                .into(ivProfileAvatar)
        }
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