package ru.gorshenev.themesstyles.data.repositories.profile

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.GetOneUserResponse
import ru.gorshenev.themesstyles.data.repositories.chat.Reactions
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: ZulipApi) {

    fun getUser(): Single<GetOneUserResponse> {
        return api.getUser(Reactions.MY_USER_ID)
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
    }
}