package com.quicoment.demo.dto

import java.io.Serializable

data class CommentLikeRequest(val commentId: Long?): Serializable {
    val messageType = "like"
}
