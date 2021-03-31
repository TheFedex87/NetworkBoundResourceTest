package it.thefedex87.networkboundresourcestest.util

import android.util.Log
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = {},
    crossinline onFetchFailed: (Throwable) -> Unit = {},
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))


        try {
            saveFetchResult(fetch())
            onFetchSuccess()
            query().map { Resource.Success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map { Resource.Error(t, it) }
        }

    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}