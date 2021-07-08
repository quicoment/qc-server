package com.quicoment.demo.common

import com.quicoment.demo.common.error.ErrorCase

sealed class ResultOf<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultOf<T>()
    data class Error(
            val code: Int,
            val message: String
    ) : ResultOf<ErrorCase>()
}