package com.quicoment.demo.dto

import java.io.Serializable

data class CommentRequest(val content: String?, val password: String?): Serializable {
    val messageType = "register"
}
