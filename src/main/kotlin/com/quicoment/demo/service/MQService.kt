package com.quicoment.demo.service

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

    fun declarePostQueue(id: String) {
        val newQueue = Queue("${queueDomain}.post.${id}")
        rabbitAdmin.declareQueue(newQueue)
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentRegisterExchange).with("post.${id}.comment"))
        rabbitAdmin.declareBinding(
            BindingBuilder.bind(newQueue).to(commentLikeExchange).with("post.${id}.comment.#"))
    }

    fun deletePostQueue(id: String): Boolean {
        return rabbitAdmin.deleteQueue("${queueDomain}.post.${id}")
    }
}
