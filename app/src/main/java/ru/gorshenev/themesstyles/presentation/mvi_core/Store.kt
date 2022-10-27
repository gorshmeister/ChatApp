package ru.gorshenev.themesstyles.presentation.mvi_core

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.gorshenev.themesstyles.utils.Utils.plusAssign

class Store<A : BaseAction, S : BaseState, E : UiEffects>(
    private val reducer: Reducer<A, S, E>,
    private val middlewares: List<Middleware<A, S>>,
    initialState: S
) {
    private val state: BehaviorRelay<S> = BehaviorRelay.createDefault(initialState)
    private val actions: PublishRelay<A> = PublishRelay.create()
    private val effects: PublishRelay<E> = PublishRelay.create()

    fun accept(action: A) {
        actions.accept(action)
    }

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        disposable += actions
            .withLatestFrom(state, reducer::reduceToState)
            .distinctUntilChanged()
            .subscribe(state::accept)

        disposable += actions
            .withLatestFrom(state) { action, state ->
                reducer.reduceToEffect(action, state)?.let(effects::accept) ?: Unit
            }
//            .distinctUntilChanged()
            .subscribe()

        disposable += Observable.merge(
            middlewares.map { it.bind(actions, state) }
        ).subscribe(actions::accept)

        return disposable
    }


    fun bind(view: MviView<S, E>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::render)
        disposable += effects
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::handleUiEffects)

        return disposable
    }
}

