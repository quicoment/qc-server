package com.quicoment.demo.service

import com.quicoment.demo.common.error.custom.NoSuchPostException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class PostService(@Autowired private val postRepository: PostRepository) {

    fun savePost(post: Post): PostResponse {
        return postRepository.save(post).toResponseDto()
    }

    fun findPost(): List<PostResponse> {
        return postRepository.findAll().map { it.toResponseDto() }
    }

    fun findPostById(id: Long): PostResponse {
        return postRepository.findById(id).orElseThrow { NoSuchPostException() }.toResponseDto()
    }

    @Transactional
    fun updatePost(id: Long, title: String, content: String, password: String) {
        val post: Post = postRepository.findById(id).orElseThrow { NoSuchPostException() }
        post.update(title, content, password)
    }

    fun deletePost(id: Long) {
        return postRepository.deleteById(id)
    }
}
