package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.dto.comment.CommentLike
import com.quicoment.demo.dto.comment.CommentRegisterRequest
import com.quicoment.demo.dto.comment.CommentUpdateRequest
import com.quicoment.demo.service.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class CommentController(@Autowired val commentService: CommentService) {

    @PostMapping("/posts/{postId}/comments")
    fun registerComment(@PathVariable postId: Long, @RequestBody commentRegister: CommentRegisterRequest): ResponseEntity<ResultOf<*>> {
        commentRegister.content ?: throw InvalidFieldException()
        commentRegister.password ?: throw InvalidFieldException()

        commentService.registerComment(commentRegister.toRegisterDto(postId))
        return ResponseEntity.accepted().build()
    }

    @PatchMapping("/comments/{commentId}/like")
    fun likeComment(@PathVariable commentId: String): ResponseEntity<ResultOf<*>> {
        commentService.likeComment(CommentLike(commentId))
        return ResponseEntity.accepted().build()
    }

    @PutMapping("/comments/{commentId}")
    fun updateComment(@PathVariable commentId: String, @RequestBody newComment: CommentUpdateRequest): ResponseEntity<ResultOf<*>> {
        newComment.password ?: throw InvalidFieldException()
        newComment.content ?: throw InvalidFieldException()

        // certificate password logic

        commentService.updateComment(newComment.toUpdateDto(commentId))
        return ResponseEntity.accepted().build()
    }
}
