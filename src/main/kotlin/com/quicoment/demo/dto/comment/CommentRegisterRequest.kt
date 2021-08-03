package com.quicoment.demo.dto.comment

data class CommentRegisterRequest(val content: String?, val password: String?) {
    fun toRegisterDto(postId: Long): CommentRegister = CommentRegister(postId, this.content, this.password)
}
