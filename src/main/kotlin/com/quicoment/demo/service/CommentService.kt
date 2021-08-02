package com.quicoment.demo.service

import com.quicoment.demo.dto.CommentLikeRequest
import com.quicoment.demo.dto.CommentRegisterRequest
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.DirectExchange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentService(@Autowired private val rabbitTemplate: AmqpTemplate,
                     @Autowired private val commentExchange: DirectExchange) {

    fun registerComment(postId: Long, commentRegisterRequest: CommentRegisterRequest) {
        rabbitTemplate.convertAndSend(commentExchange.name, "register", commentRegisterRequest)
    }

    fun likeComment(postId: Long, commentLikeRequest: CommentLikeRequest) {
        rabbitTemplate.convertAndSend(commentExchange.name, "like", commentLikeRequest)
    }
}
