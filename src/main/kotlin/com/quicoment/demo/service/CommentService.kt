package com.quicoment.demo.service

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CommentService(private val rabbitTemplate: AmqpTemplate) {

    @Transactional
    fun enqueueComment(postId: Long, message: String) {
        rabbitTemplate.convertAndSend("exchangeName", "post.${postId}", message)
    }
}
