package ru.gorshenev.themesstyles.presentation.ui.profile.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import ru.gorshenev.themesstyles.data.repositories.profile.ProfileRepository
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    val repository: ProfileRepository,
) : ViewModel() {

    private val initialState: ProfileState = ProfileState.Loading

    private val _states: MutableSharedFlow<ProfileState> = MutableSharedFlow(extraBufferCapacity = 10)

    val states: StateFlow<ProfileState>
        get() = _states.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = initialState
        )

    private val _effects: MutableSharedFlow<ProfileEffect> = MutableSharedFlow()

    val effects: Flow<ProfileEffect>
        get() = _effects

    private val actions: MutableSharedFlow<ProfileAction> = MutableSharedFlow()

    private val reducer: Reducer<ProfileState, ProfileChange> = { state, change ->
        when (change) {
            ProfileChange.StartLoading -> {
                ProfileState.Loading
            }
            is ProfileChange.LoadResult -> {
                ProfileState.Result(
                    profileName = change.profileName,
                    avatarUrl = change.avatarUrl
                )
            }
            is ProfileChange.LoadError -> {
                ProfileState.Error
            }
        }
    }

    init {
        listOf(
            bindLoadActions(),
        )
            .merge()
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .catch { er -> Log.d("qweqwe", er.message.orEmpty()) }
            .onEach(_states::emit)
            .launchIn(viewModelScope)
    }

    fun accept(action: ProfileAction) {
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    private fun bindLoadActions(): Flow<ProfileChange> {
        return actions.filterIsInstance<ProfileAction.LoadProfile>()
            .map<ProfileAction, ProfileChange> {
                try {
                    val info = repository.getUser().await()
                    //TODO Handle error?
                    ProfileChange.LoadResult(info.members.firstName, info.members.avatarUrl)
                } catch (e: Exception) {
                    _effects.emit(ProfileEffect.SnackBar(e))
                    ProfileChange.LoadError(e)
                }
            }
            .onStart { emit(ProfileChange.StartLoading) }
    }

}