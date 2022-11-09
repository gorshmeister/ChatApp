package ru.gorshenev.themesstyles.presentation.ui.channels.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.stream.StreamMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.stream.StreamRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamInternalAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamState

class LoadMiddleware(private val repository: StreamRepository) :
    Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.UploadStreams::class.java)
            .flatMap { action ->
                repository.getStreams(action.streamType)
                    .map<StreamInternalAction> { StreamInternalAction.LoadResult(it.toUi(action.streamType)) }
                    .onErrorReturn { StreamInternalAction.LoadError(it) }
                    .startWith(StreamInternalAction.StartLoading)
            }
    }
}
