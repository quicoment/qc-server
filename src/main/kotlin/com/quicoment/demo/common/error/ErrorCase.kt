package com.quicoment.demo.common.error

import org.springframework.http.HttpStatus

enum class ErrorCase(private val code: Int, private val message: String) {
    INVALID_METHOD(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 메소드입니다. "),
    INVALID_TYPE(HttpStatus.BAD_REQUEST.value(), "잘못된 타입입니다. "),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(), "잘못된 형식입니다. "),
    INVALID_FIELD(HttpStatus.BAD_REQUEST.value(), "필수항목을 입력해주세요. "),
    NO_SUCH_POST(HttpStatus.BAD_REQUEST.value(), "해당 게시물이 존재하지 않습니다. "),
    CONNECTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 오류가 있습니다. 잠시 후 다시 시도해 주세요. "),
    SAVE_POST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시글 저장에 실패했습니다. ");

    fun getMessage(): String {
        return this.message
    }

    fun getCode(): Int {
        return code
    }

    override fun toString(): String {
        return "$code: $message"
    }
}
