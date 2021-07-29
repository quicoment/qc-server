package com.quicoment.demo.service

import com.quicoment.demo.dto.CommentLikeRequest
import com.quicoment.demo.dto.CommentRequest
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class CommentService(@Autowired private val rabbitTemplate: AmqpTemplate,
                     @Autowired private val commentRegisterExchange: TopicExchange,
                     @Autowired private val commentLikeExchange: TopicExchange) {

    fun registerComment(postId: Long, commentRequest: CommentRequest) {
        rabbitTemplate.convertAndSend(commentRegisterExchange.name, "post.${postId}.comment", commentRequest)
    }

    fun likeComment(postId: Long, commentId: Long) {
        rabbitTemplate.convertAndSend(commentLikeExchange.name, "post.${postId}.comment.${commentId}", CommentLikeRequest(commentId))
    }
}
