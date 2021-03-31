package it.thefedex87.networkboundresourcestest.data

import android.provider.Settings
import it.thefedex87.networkboundresourcestest.data.db.User
import it.thefedex87.networkboundresourcestest.data.db.UserDao
import it.thefedex87.networkboundresourcestest.util.Resource
import it.thefedex87.networkboundresourcestest.util.networkBoundResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(
    private val userDao: UserDao
) {
    // Flow to dispatch user from ROOM DB
    fun users(query: String) = userDao.users(query)
    val fetchUsersState = MutableStateFlow<Resource<List<User>>>(Resource.Empty())
    suspend fun fetchUsers(forceFetch: Boolean) {
        val data = users("").first()

        if(data.isNullOrEmpty() || forceFetch) {
            try {
                fetchUsersState.value = Resource.Loading(data)
                // Fake API call
                delay(3000)
                val users = listOf(
                    User("Paolo", "Rossi", "Paulu"),
                    User("Francesca", "Bianchi", "Frax"),
                    User("Luca", "Neri", "Lux"),
                    User("Federico", "Verdi", "Fedex"),
                )
                userDao.deleteAll()
                userDao.insertList(users)
            } catch (t: Throwable) {
                fetchUsersState.value = Resource.Error(t, data)
            }
        }

        fetchUsersState.value = Resource.Success(data)
    }
}