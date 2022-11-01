package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware

class StreamUploadMiddleware(private val repository: StreamRepository) :
    Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.UploadStreams::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .flatMap { (action, _) ->
                repository.getStreams(action.streamType)
                    .map<StreamInternalAction> { StreamInternalAction.LoadResult(it.toUi(action.streamType)) }
                    .onErrorReturn { StreamInternalAction.LoadError(it) }
                    .startWith(StreamInternalAction.StartLoading)
            }
    }
}
