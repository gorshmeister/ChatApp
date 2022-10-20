package ru.gorshenev.themesstyles.data.repositories.people

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.GetUserPresenceResponse
import ru.gorshenev.themesstyles.data.network.model.PeopleStatusResponse
import ru.gorshenev.themesstyles.data.network.model.UserResponse
import ru.gorshenev.themesstyles.domain.model.people.PeopleModel
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi

class PeopleRepository(
    private val api: ZulipApi,
    private val executionScheduler: Scheduler = Schedulers.io()
) {

    fun getUsers(): Single<MutableList<PeopleModel>> {
        return api.getUsers()
            .flatMap { getUserResponse ->
                Observable.fromIterable(getUserResponse.members)
                    .flatMapSingle { user ->
                        Single.zip(
                            Single.just(user),
                            api.getUserPresence(user.userId),
                            ::createPeopleModel
                        )
                    }.toList()
            }
            .onErrorReturn { emptyList() }
            .subscribeOn(executionScheduler)
    }

    private fun createPeopleModel(user: UserResponse, presence: GetUserPresenceResponse) =
        PeopleModel(
            id = user.userId,
            name = user.firstName,
            email = user.email,
            avatar = user.avatarUrl,
            status = getStatus(presence)
        )

    private fun getStatus(presence: GetUserPresenceResponse): PeopleUi.PeopleStatus {
        return when (presence.presence.aggregated.status) {
            PeopleStatusResponse.ONLINE -> PeopleUi.PeopleStatus.ONLINE
            PeopleStatusResponse.IDLE -> PeopleUi.PeopleStatus.IDLE
            PeopleStatusResponse.OFFLINE -> PeopleUi.PeopleStatus.OFFLINE
        }
    }
}