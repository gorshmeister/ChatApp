package ru.gorshenev.themesstyles.presentation.ui.people

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.GetUserPresence
import ru.gorshenev.themesstyles.data.network.model.User
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

    private fun createPeopleUiWithStatus(user: User, presence: GetUserPresence) =
        PeopleUi(
            id = user.userId,
            name = user.firstName,
            email = user.email,
            avatar = user.avatarUrl,
            status = getStatus(presence)
        )

    private fun getStatus(presence: GetUserPresence): PeopleUi.PeopleStatus {
        return when (presence.presence.aggregated.status) {
            "active" -> PeopleUi.PeopleStatus.ONLINE
            "idle" -> PeopleUi.PeopleStatus.IDLE
            else -> PeopleUi.PeopleStatus.OFFLINE
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
        return Single.fromCallable {
            return@fromCallable when {
                searchText.isEmpty() -> cachedItems
                else -> cachedItems.filter { item ->
                    item is PeopleUi && (item.name.contains(
                        searchText,
                        true
                    ) || item.email.contains(searchText, true))
                }
            }
        }
    }

    //    fun loadPeople(count: Int) {
//        PeopleDataSource.getPeople(count)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { view.showLoading() }
//            .subscribe(
//                { people ->
//                    view.showItems(people)
//                    cachedItems = people
//                },
//                { error -> view.showError(error) },
//                { view.stopLoading() })
//            .apply { compositeDisposable.add(this) }
//    }


}