package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.dto.CommentLikeRequest
import com.quicoment.demo.dto.CommentRegisterRequest
import com.quicoment.demo.dto.CommentRequest
import com.quicoment.demo.service.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class CommentController(@Autowired val commentService: CommentService) {

    @PostMapping("/posts/{postId}/comments")
    fun registerComment(@PathVariable postId: Long, @RequestBody comment: CommentRequest): ResponseEntity<ResultOf<*>> {
        comment.content ?: throw InvalidFieldException()
        comment.password ?: throw InvalidFieldException()

        commentService.registerComment(postId, CommentRegisterRequest(postId, comment.content, comment.password))
        return ResponseEntity.ok().build()
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    fun likeComment(@PathVariable postId: Long, @PathVariable commentId: Long): ResponseEntity<ResultOf<*>> {
        commentService.likeComment(postId, CommentLikeRequest(commentId))
        return ResponseEntity.ok().build()
    }
}
