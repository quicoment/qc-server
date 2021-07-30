package com.quicoment.demo.service

import com.quicoment.demo.dto.QueueDelete
import com.quicoment.demo.dto.QueueRequest
import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service

@Service
@PropertySource("classpath:rabbit-mq.yml")
class MQService(
    @Autowired private val rabbitAdmin: AmqpAdmin,
    @Autowired private val commentRegisterExchange: DirectExchange,
    @Autowired private val commentLikeExchange: TopicExchange,
    @Value("\${rabbitmq.queue-name-domain}") private val queueDomain: String) {

    fun declarePostQueue(id: String): QueueRequest {
        val queueName = "${queueDomain}.post.${id}"
        val newQueue = Queue(queueName)
        rabbitAdmin.declareQueue(newQueue)

        val registerExchangeName = "post.${id}.comment"
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentRegisterExchange).with(registerExchangeName))

        val likeExchangeName = "post.${id}.comment.#"
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentLikeExchange).with(likeExchangeName))
        return QueueRequest(queueName, registerExchangeName, likeExchangeName)
    }

    fun deletePostQueue(id: String): QueueDelete {
        val queueName = "${queueDomain}.post.${id}"
        rabbitAdmin.deleteQueue(queueName)
        return QueueDelete(queueName)
    }
}
