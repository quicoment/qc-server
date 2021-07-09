package com.quicoment.demo.service

import com.quicoment.demo.domain.Post
import com.quicoment.demo.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostService(@Autowired private val postRepository: PostRepository) {

    fun savePost(post: Post): Post {
        return postRepository.save(post)
    }

    fun findPostById(id: Long): Post? {
        return postRepository.findById(id).orElse(null)
    }
}
