package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.post.PostRequest
import com.quicoment.demo.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URI

@Controller
class PostController(@Autowired val postService: PostService) {

    @PostMapping("/posts")
    fun savePost(@RequestBody post: PostRequest): ResponseEntity<ResultOf<*>> {
        post.title ?: throw InvalidFieldException()
        post.content ?: throw InvalidFieldException()
        post.password ?: throw InvalidFieldException()

        val uri = URI.create("/posts/${postService.savePost(Post(post.title, post.content, post.password))}")
        return ResponseEntity.created(uri).build()
    }

    @GetMapping("/posts")
    fun findAllPosts(): ResponseEntity<ResultOf<*>> {
        return ResponseEntity.ok(ResultOf.Success(postService.findAllPosts()))
    }

    @GetMapping("/posts/{id}")
    fun findPostById(@PathVariable("id") id: Long): ResponseEntity<ResultOf<*>> {
        return ResponseEntity.ok(ResultOf.Success(postService.findPostById(id)))
    }

    @PutMapping("/posts/{id}")
    fun updatePost(@PathVariable("id") id: Long, @RequestBody post: PostRequest): ResponseEntity<ResultOf<*>> {
        post.title ?: throw InvalidFieldException()
        post.content ?: throw InvalidFieldException()
        post.password ?: throw InvalidFieldException()

        postService.updatePost(id, post.title, post.content, post.password)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(@PathVariable("id") id: Long): ResponseEntity<ResultOf<*>> {
        postService.deletePost(id)
        return ResponseEntity.noContent().build()
    }
}
