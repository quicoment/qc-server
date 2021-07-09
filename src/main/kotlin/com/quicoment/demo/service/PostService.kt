package com.quicoment.demo.service

import com.quicoment.demo.common.error.custom.NoSuchPostException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PostService(@Autowired private val postRepository: PostRepository) {

    fun savePost(post: Post): Post {
        return postRepository.save(post)
    }

    fun findPost(): List<Post> {
        return postRepository.findAll()
    }

    fun findPostById(id: Long): Post? {
        return postRepository.findById(id).orElse(null)
    }

    fun updatePost(id: Long, title: String, content: String, password: String): Post {
        val post: Post = postRepository.findById(id).orElseThrow { NoSuchPostException() }
        post.update(title, content, password)
        return postRepository.save(post)
    }

    fun deletePost(id: Long) {
        return postRepository.deleteById(id)
    }
}
