package com.quicoment.demo.controller

import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostRequest
import com.quicoment.demo.result.ErrorCase
import com.quicoment.demo.result.ResultOf
import com.quicoment.demo.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI

@Controller
class PostController(@Autowired val postService: PostService) {

    @PostMapping("/posts")
    fun savePost(@RequestBody post: PostRequest): ResponseEntity<ResultOf<Any>> {
        val errorResponse = ResultOf.Error<ErrorCase>(ErrorCase.INVALID_FIELD.name, ErrorCase.INVALID_FIELD.getMessage())

        post.title ?: return ResponseEntity.badRequest().body(errorResponse)
        post.content ?: return ResponseEntity.badRequest().body(errorResponse)
        post.password ?: return ResponseEntity.badRequest().body(errorResponse)

        val id = postService.savePost(Post(post.title, post.content, post.password)).id
                ?: return ResponseEntity.internalServerError().body(ResultOf.Error(ErrorCase.CONNECTION_FAIL.name, ErrorCase.CONNECTION_FAIL.getMessage()))

        return ResponseEntity.created(URI.create("/posts/${id}")).build()
    }
}