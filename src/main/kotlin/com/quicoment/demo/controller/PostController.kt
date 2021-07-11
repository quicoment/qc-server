package com.quicoment.demo.controller

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.common.error.custom.NoSuchPostException
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostRequest
import com.quicoment.demo.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.HttpServerErrorException
import java.net.URI

@Controller
class PostController(@Autowired val postService: PostService) {

    @PostMapping("/posts")
    fun savePost(@RequestBody post: PostRequest): ResponseEntity<ResultOf<*>> {
        post.title ?: throw InvalidFieldException()
        post.content ?: throw InvalidFieldException()
        post.password ?: throw InvalidFieldException()

        val id = postService.savePost(Post(post.title, post.content, post.password)).id
                ?: throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)

        return ResponseEntity.created(URI.create("/posts/${id}")).build()
    }

    @GetMapping("/posts")
    fun findPost(): ResponseEntity<ResultOf<*>> {
        return ResponseEntity.ok(ResultOf.Success(postService.findPost()))
    }

    @GetMapping("/posts/{id}")
    fun findPostById(@PathVariable("id") id: Long?): ResponseEntity<ResultOf<*>> {
        id ?: throw InvalidFieldException()

        val post: Post = postService.findPostById(id) ?: throw NoSuchPostException()
        return ResponseEntity.ok(ResultOf.Success(post))
    }


    @PutMapping("/posts/{id}")
    fun updatePost(@PathVariable("id") id: Long?, @RequestBody post: PostRequest): ResponseEntity<ResultOf<*>> {
        id ?: throw InvalidFieldException()
        post.title ?: throw InvalidFieldException()
        post.content ?: throw InvalidFieldException()
        post.password ?: throw InvalidFieldException()

        postService.updatePost(id, post.title, post.content, post.password)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(@PathVariable("id") id: Long?): ResponseEntity<ResultOf<*>> {
        id ?: throw InvalidFieldException()

        postService.deletePost(id)
        return ResponseEntity.ok(ResultOf.Success(postService.findPost()))
    }
}
