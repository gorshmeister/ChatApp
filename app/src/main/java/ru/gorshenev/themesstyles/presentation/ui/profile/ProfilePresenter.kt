package ru.gorshenev.themesstyles.presentation.ui.profile

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import ru.gorshenev.themesstyles.presentation.base.presenter.RxPresenter

class ProfilePresenter(private val repository: ProfileRepository) :
    RxPresenter<ProfileView>() {

    fun uploadProfile() {
        repository.getUser()
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { view?.stopLoading() }
            .doOnError { view?.showEmptyState() }
            .subscribe(
                { view?.setProfile(name = it.members.firstName, avatarUrl = it.members.avatarUrl) },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }

}
