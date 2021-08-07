package com.quicoment.demo.dto.comment

import java.io.Serializable

data class CommentLike(val postId: Long?, val commentId: String?, val userId: String?): Serializable {
    val messageType = "like"
}
