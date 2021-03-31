package it.thefedex87.networkboundresourcestest.ui.main

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.networkboundresourcestest.data.Repository
import it.thefedex87.networkboundresourcestest.data.db.User
import it.thefedex87.networkboundresourcestest.util.Refresh
import it.thefedex87.networkboundresourcestest.util.Resource
import kotlinx.coroutines.Job
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

    private val _mainState = MutableStateFlow<MainStateEvent>(MainStateEvent.Empty)
    val mainState: LiveData<MainStateEvent> get() = _mainState.asLiveData()

    val query: MutableStateFlow<String> = MutableStateFlow("")
    private val refresh: MutableSharedFlow<Refresh> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            repository.fetchUsersState.collect {
                when(it) {
                    is Resource.Success -> {
                        _mainState.value = MainStateEvent.FetchUsersSuccess
                    }
                    is Resource.Loading -> {
                        _mainState.value = MainStateEvent.FetchUsersLoading
                    }
                    is Resource.Error -> {
                        _mainShared.emit(MainStateEvent.FetchUsersError(it.throwable!!))
                    }
                    is Resource.Empty -> {
                        // Do Nothing
                    }
                }
            }
        }

        getUsers(false)
    }

    val users = query.flatMapLatest {
        repository.users(it)
    }.asLiveData()

    private fun getUsers(
        forceRefresh: Boolean
    ) {
        viewModelScope.launch {
            repository.fetchUsers(forceRefresh)
        }
    }

    fun updateUsers() {
        getUsers(true)
    }
}

sealed class MainStateEvent {
    object FetchUsersSuccess : MainStateEvent()
    object FetchUsersLoading : MainStateEvent()
    data class FetchUsersError(val t: Throwable) : MainStateEvent()
    object Empty : MainStateEvent()
}