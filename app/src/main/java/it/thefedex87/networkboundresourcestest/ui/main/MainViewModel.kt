package it.thefedex87.networkboundresourcestest.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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

    val query: MutableStateFlow<String> = MutableStateFlow("")
    private val refresh: MutableSharedFlow<Refresh> = MutableSharedFlow()

    init {
        viewModelScope.launch {
            query.collect {
                getUsers(
                    it,
                    Refresh.DEFAULT
                )
            }
        }
    }

    val users: MutableLiveData<Resource<List<User>>> = MutableLiveData()

    fun onStart() {
        getUsers(
            query.value,
            Refresh.DEFAULT
        )
    }

    private lateinit var job: Job
    private fun getUsers(
        query: String,
        refresh: Refresh
    ) {
        if(this::job.isInitialized) {
            job.cancel()
        }
        job = viewModelScope.launch {
            repository.users(
                query = query,
                forceReload = refresh == Refresh.FORCE,
                onFetchError = {
                    viewModelScope.launch {
                        _mainShared.emit(MainStateEvent.Error(it))
                    }
                }
            ).collect {
                users.value = it
            }
        }
    }

    fun updateUsers() {
        getUsers(
            query.value,
            Refresh.FORCE
        )
    }
}

sealed class MainStateEvent {
    data class Error(val t: Throwable) : MainStateEvent()
}