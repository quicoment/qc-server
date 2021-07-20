package com.quicoment.demo.service

import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@PropertySource("classpath:rabbit-mq.yml")
@Transactional(readOnly = true)
class PostService(
    @Autowired private val postRepository: PostRepository,
    @Autowired private val rabbitAdmin: AmqpAdmin,
    @Autowired private val commentRegisterExchange: DirectExchange,
    @Autowired private val commentLikeExchange: TopicExchange,
    @Value("\${rabbitmq.queue-name-domain}") private val queueDomain: String) {

    @Transactional
    fun savePost(post: Post): Long {
        val postId = postRepository.save(post).toResponseDto().id
            ?: throw FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage())

        val newQueue = Queue("${queueDomain}.post.${postId}")
        rabbitAdmin.declareQueue(newQueue)
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentRegisterExchange).with("post.${postId}.comment"))
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentLikeExchange).with("post.${postId}.comment.#"))
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
        rabbitAdmin.deleteQueue("${queueDomain}.post.${id}")
    }
}
