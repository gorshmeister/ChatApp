package ru.gorshenev.themesstyles.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.gorshenev.themesstyles.presentation.presenter.Presenter

abstract class MvpFragment<View, P : Presenter<View>>(contentLayoutId: Int) :
    Fragment(contentLayoutId), ViewCallback<View, P> {

    private val mvpHelper: MvpHelper<View, P> by lazy { MvpHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpHelper.create()
    }

    override fun onDestroyView() {
        val isFinishing = isRemoving || requireActivity().isFinishing
        mvpHelper.destroy(isFinishing)
        super.onDestroyView()
    }
}