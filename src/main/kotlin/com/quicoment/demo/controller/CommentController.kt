package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.dto.CommentRequest
import com.quicoment.demo.service.CommentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class CommentController(@Autowired val commentService: CommentService) {

    @PostMapping("/posts/{postId}/comments")
    fun newComment(@PathVariable postId: Long, @RequestBody comment: CommentRequest): ResponseEntity<ResultOf<*>> {
        comment.content ?: throw InvalidFieldException()

        commentService.enqueueComment(postId, comment.content)
        return ResponseEntity.ok().build()
    }
}
