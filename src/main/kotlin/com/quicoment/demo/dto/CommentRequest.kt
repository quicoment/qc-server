package com.quicoment.demo.dto

data class CommentRequest(val content: String?, val password: String?) {
    val messageType = "register"
}
