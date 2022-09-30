package ru.gorshenev.themesstyles.presentation.ui.profile

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.repositories.chat.Reactions.MY_USER_ID
import java.util.concurrent.TimeUnit

class ProfilePresenter(private val view: ProfileView) {

    private val compositeDisposable = CompositeDisposable()

    private val api = Network.api


    fun uploadProfile() {
        api.getUser(MY_USER_ID)
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doAfterSuccess { view.stopLoading() }
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
