package com.quicoment.demo.dto

data class CommentLikeRequest(val commentId: Long?) {
    val messageType = "like"
}
