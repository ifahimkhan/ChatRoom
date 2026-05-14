package com.fahim.chatroom.core.error

import kotlin.coroutines.cancellation.CancellationException

/** Sealed result type used across domain and data layers. */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

/** Runs [block], mapping any throwable (except cancellation) into an [AppResult.Failure]. */
suspend fun <T> appResultOf(block: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        AppResult.Failure(t as? AppError ?: AppError.Unknown(t))
    }