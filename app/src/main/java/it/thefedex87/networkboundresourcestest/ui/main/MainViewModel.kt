package it.thefedex87.networkboundresourcestest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.networkboundresourcestest.data.Repository
import it.thefedex87.networkboundresourcestest.util.Refresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _mainShared = MutableSharedFlow<MainStateEvent>()
    val mainShared get() = _mainShared.asLiveData()

    val query: MutableStateFlow<String> = MutableStateFlow("")
    private val refresh: MutableSharedFlow<Refresh> = MutableSharedFlow()

    val users = combine(
        query,
        refresh
    ) { query, refresh ->
        Pair(query, refresh)
    }.flatMapLatest { (query, refresh) ->
        repository.users(
            query = query,
            forceReload = refresh == Refresh.FORCE,
            onFetchError = {
                viewModelScope.launch {
                    _mainShared.emit(MainStateEvent.Error(it))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null).asLiveData()

    fun onStart() {
        viewModelScope.launch {
            delay(1)
            refresh.emit(Refresh.DEFAULT)
        }
    }

    fun updateUsers() {
        viewModelScope.launch {
            refresh.emit(Refresh.FORCE)
        }
    }
}

sealed class MainStateEvent {
    data class Error(val t: Throwable) : MainStateEvent()
}