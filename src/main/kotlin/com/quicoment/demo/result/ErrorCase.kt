package com.quicoment.demo.result

enum class ErrorCase {
    INVALID_FIELD {
        override fun getMessage(): String { return "필수항목을 입력해주세요. " }
    };

    abstract fun getMessage(): String
}
