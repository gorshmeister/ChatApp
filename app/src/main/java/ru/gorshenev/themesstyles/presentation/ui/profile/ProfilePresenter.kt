package ru.gorshenev.themesstyles.presentation.ui.profile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.presentation.presenter.RxPresenter

class ProfilePresenter(private val repository: ProfileRepository) :
    RxPresenter<ProfileView>(ProfileView::class.java) {

    private val compositeDisposable = CompositeDisposable()

    fun uploadProfile() {
        repository.getUser()
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { view.stopLoading() }
            .subscribe(
                { response ->
                    view.setProfile(
                        name = response.members.firstName,
                        avatarUrl = response.members.avatarUrl
                    )
                },
                { err -> view.showError(err) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun onClear() {
        compositeDisposable.clear()
    }
}
