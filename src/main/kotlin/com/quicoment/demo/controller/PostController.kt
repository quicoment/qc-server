package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostRequest
import com.quicoment.demo.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.net.URI

@Controller
class PostController(@Autowired val postService: PostService) {

    @PostMapping("/posts")
    fun savePost(@RequestBody post: PostRequest): ResponseEntity<ResultOf<*>> {
        val errorResponse = ResultOf.Error(ErrorCase.INVALID_FIELD.getCode(), ErrorCase.INVALID_FIELD.getMessage())

        post.title ?: return ResponseEntity.badRequest().body(errorResponse)
        post.content ?: return ResponseEntity.badRequest().body(errorResponse)
        post.password ?: return ResponseEntity.badRequest().body(errorResponse)

        val id = postService.savePost(Post(post.title, post.content, post.password)).id
                ?: return ResponseEntity.internalServerError().body(ResultOf.Error(ErrorCase.CONNECTION_FAIL.getCode(), ErrorCase.CONNECTION_FAIL.getMessage()))

        return ResponseEntity.created(URI.create("/posts/${id}")).build()
    }

    @GetMapping("/posts/{id}")
    fun findPostById(@PathVariable("id") id: Long?): ResponseEntity<ResultOf<*>> {
        id
                ?: return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.INVALID_FIELD.getCode(), ErrorCase.INVALID_FIELD.getMessage()))

        val post = postService.findPostById(id)
                ?: return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.NO_SUCH_POST.getCode(), ErrorCase.NO_SUCH_POST.getMessage()))
        return ResponseEntity.ok(ResultOf.Success(post))
    }
}
