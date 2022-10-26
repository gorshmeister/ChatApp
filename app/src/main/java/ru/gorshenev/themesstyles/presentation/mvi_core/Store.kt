package ru.gorshenev.themesstyles.presentation.mvi_core

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.gorshenev.themesstyles.utils.Utils.plusAssign

class Store<A : BaseAction, S : BaseState, E : UiEffects>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<A, S>>,
    initialState: S
) {
    private val state = BehaviorRelay.createDefault(initialState)
    private val actions = PublishRelay.create<A>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        disposable += actions
            .withLatestFrom(state) { action, state ->
                reducer.reduce(state, action)
            }
            .distinctUntilChanged()
            .subscribe(state::accept)

        disposable += Observable.merge(
            middlewares.map { it.bind(actions, state) }
        ).subscribe(actions::accept)

        return disposable
    }


    fun bind(view: MviView<A, S, E>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::render)
        disposable += view.effects
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::handleUiEffects)
        disposable += view.actions.subscribe(actions::accept)

        return disposable
    }
}

