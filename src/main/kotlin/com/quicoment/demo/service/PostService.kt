package com.quicoment.demo.service

import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    @Autowired private val postRepository: PostRepository,
    @Autowired private val mqService: MQService) {

    @Transactional
    fun savePost(post: Post): Long {
        val postId = postRepository.save(post).toResponseDto().id
            ?: throw FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage())
        mqService.declarePostQueue(postId.toString())
        return postId
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
        mqService.deletePostQueue(id.toString())
    }
}
