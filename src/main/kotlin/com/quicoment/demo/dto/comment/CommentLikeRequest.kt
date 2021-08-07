package com.quicoment.demo.dto.comment

data class CommentLikeRequest(val userId: String?) {
    fun toLikeDto(postId: Long, commentId: String): CommentLike = CommentLike(postId, commentId, this.userId)
}
