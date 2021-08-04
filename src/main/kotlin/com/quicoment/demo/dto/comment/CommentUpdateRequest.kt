package com.quicoment.demo.dto.comment

data class CommentUpdateRequest(val password: String?, val content: String?) {
    fun toUpdateDto(postId: Long, commentId: String): CommentUpdate = CommentUpdate(postId, commentId, this.content)
}
