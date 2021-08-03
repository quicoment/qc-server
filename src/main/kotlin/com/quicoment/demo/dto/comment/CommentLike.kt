package com.quicoment.demo.dto.comment

import java.io.Serializable

data class CommentLike(val commentId: String?): Serializable {
    val messageType = "like"
}
