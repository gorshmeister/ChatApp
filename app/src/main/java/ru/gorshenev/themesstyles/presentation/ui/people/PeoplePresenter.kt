package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.GetUserPresenceResponse
import ru.gorshenev.themesstyles.data.network.model.PeopleStatusResponse
import ru.gorshenev.themesstyles.data.network.model.UserResponse
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import java.util.concurrent.TimeUnit

class PeoplePresenter(private val view: PeopleView) {

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()

    private val searchSubject: PublishSubject<String> = PublishSubject.create()

    private val api: ZulipApi = Network.api


    init {
        searchSubject
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { searchTxt -> initUserSearch(cachedItems, searchTxt) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { filteredPeople -> view.showItems(filteredPeople) },
                { error -> view.showError(error) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun loadPeople() {
        api.getUsers()
            .flatMap { getUserResponse ->
                Observable.fromIterable(getUserResponse.members)
                    .flatMapSingle { user ->
                        Single.zip(
                            Single.just(user),
                            api.getUserPresence(user.userId),
                            ::createPeopleUiWithStatus
                        )
                    }.toList()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.showLoading() }
            .doAfterSuccess { view.stopLoading() }
            .subscribe(
                { people ->
                    view.showItems(people)
                    cachedItems = people
                },
                { error -> view.showError(error) },
            ).apply { compositeDisposable.add(this) }
    }

    private fun createPeopleUiWithStatus(user: UserResponse, presence: GetUserPresenceResponse) =
        PeopleUi(
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


    fun searchPeople(query: String) {
        searchSubject.onNext(query)
    }

    fun onClear() {
        compositeDisposable.clear()
    }

    private fun initUserSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        val peopleUiList = cachedItems.filterIsInstance<PeopleUi>()

        return Single.fromCallable {
            peopleUiList.filter { people ->
                val nameContainsSearchText = people.name.contains(searchText, true)
                val emailContainsSearchText = people.email.contains(searchText, true)

                when (true) {
                    searchText.isNotEmpty() -> nameContainsSearchText || emailContainsSearchText
                    else -> true
                }
            }
        }
//        return Single.fromCallable {
//            return@fromCallable when {
//                searchText.isEmpty() -> cachedItems
//                else -> cachedItems.filter { item ->
//                    item is PeopleUi && (item.name.contains(
//                        searchText,
//                        true
//                    ) || item.email.contains(searchText, true))
//                }
//            }
//        }
    }

}