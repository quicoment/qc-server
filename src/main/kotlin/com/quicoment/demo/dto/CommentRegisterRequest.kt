package com.quicoment.demo.dto

import java.io.Serializable

data class CommentRegisterRequest(val postId: Long?, val content: String?, val password: String?) : Serializable {
    val messageType = "register"
}
