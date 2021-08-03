package com.quicoment.demo.service

import com.quicoment.demo.dto.comment.CommentLike
import com.quicoment.demo.dto.comment.CommentRegister
import com.quicoment.demo.dto.comment.CommentUpdate
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.DirectExchange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommentService(@Autowired private val rabbitTemplate: AmqpTemplate, @Autowired private val commentExchange: DirectExchange) {

    fun registerComment(commentRegister: CommentRegister) {
        rabbitTemplate.convertAndSend(commentExchange.name, "register", commentRegister)
    }

    fun likeComment(commentLike: CommentLike) {
        rabbitTemplate.convertAndSend(commentExchange.name, "like", commentLike)
    }

    fun updateComment(commentUpdate: CommentUpdate) {
        rabbitTemplate.convertAndSend(commentExchange.name, "update", commentUpdate)
    }
}
