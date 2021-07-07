package com.quicoment.demo.result

enum class ErrorCase {
    INVALID_FIELD {
        override fun getMessage(): String { return "필수항목을 입력해주세요. " }
    },
    CONNECTION_FAIL {
        override fun getMessage(): String { return "데이터베이스 등 인터넷 연결에 문제가 있습니다. " }
    };

    abstract fun getMessage(): String
}
