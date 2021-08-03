package com.quicoment.demo.service

import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.post.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(@Autowired private val postRepository: PostRepository) {
    @Transactional
    fun savePost(post: Post): Long {
        return postRepository.save(post).toResponseDto().id
            ?: throw FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage())
    }

    fun findAllPosts(): List<PostResponse> {
        return postRepository.findAll().map { it.toResponseDto() }
    }

    fun findPostById(id: Long): PostResponse {
        return postRepository.findById(id)
            .orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }
            .toResponseDto()
    }

    @Transactional
    fun updatePost(id: Long, title: String, content: String, password: String) {
        postRepository.findById(id)
            .orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }
            .update(title, content, password)
    }

    @Transactional
    fun deletePost(id: Long) {
        postRepository.findById(id).orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }
            .let { postRepository.delete(it) }
    }
}
