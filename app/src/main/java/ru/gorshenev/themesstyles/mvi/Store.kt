package ru.gorshenev.themesstyles.mvi

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class Store<A : Any, S : Any>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<A, S>>,
    private val initialState: S
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


    fun bind(view: MviView<A, S>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::render)
        disposable += view.actions.subscribe(actions::accept)

        return disposable
    }
}

private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
