package com.quicoment.demo.result

sealed class ResultOf<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultOf<T>()
    data class Error<out T : Any>(
            val code: String?,
            val message: String?
    ) : ResultOf<T>()
}