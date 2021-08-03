package com.quicoment.demo.dto.comment

data class CommentUpdateRequest(val password: String?, val content: String?) {
    fun toUpdateDto(commentId: String): CommentUpdate = CommentUpdate(commentId, this.content)
}
