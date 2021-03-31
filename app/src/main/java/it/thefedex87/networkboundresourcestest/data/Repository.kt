package it.thefedex87.networkboundresourcestest.data

import android.provider.Settings
import it.thefedex87.networkboundresourcestest.data.db.User
import it.thefedex87.networkboundresourcestest.data.db.UserDao
import it.thefedex87.networkboundresourcestest.util.networkBoundResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class Repository @Inject constructor(
    private val userDao: UserDao
) {
    fun users(
        query: String,
        forceReload: Boolean = true,
        onFetchSuccess: () -> Unit = { },
        onFetchError: (Throwable) -> Unit = { }
    ) = networkBoundResource(
        query = {
            userDao.users(query)
        },
        fetch = {
            // Fake API call
            delay(3000)
            listOf(
                User("Paolo", "Rossi", "Paulu"),
                User("Francesca", "Bianchi", "Frax"),
                User("Luca", "Neri", "Lux"),
                User("Federico", "Verdi", "Fedex"),
            )
        },
        saveFetchResult = {
            userDao.deleteAll()
            userDao.insertList(it)
        },
        shouldFetch = {
            forceReload || it == null || it.isEmpty()
        },
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            onFetchError(t)
        }
    )
}