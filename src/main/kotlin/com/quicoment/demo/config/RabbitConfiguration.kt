package com.quicoment.demo.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


@Configuration
@PropertySource("classpath:rabbit-mq.yml")
class RabbitConfiguration(
    @Value("\${rabbitmq.host}") private val RABBITMQ_HOST: String,
    @Value("\${rabbitmq.port}") private val RABBITMQ_PORT: String,
    @Value("\${rabbitmq.username}") private val USERNAME: String,
    @Value("\${rabbitmq.password}") private val PASSWORD: String,
    @Value("\${rabbitmq.exchange-name-domain}") private val EXCHANGE_NAME: String,
) {

    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(RABBITMQ_HOST, RABBITMQ_PORT.toInt())
        connectionFactory.username = USERNAME
        connectionFactory.setPassword(PASSWORD)
        return connectionFactory
    }

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): AmqpAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun commentRegisterExchange(): DirectExchange {
        return DirectExchange("${EXCHANGE_NAME}.comment.register")
    }

    @Bean
    fun commentLikeExchange(): TopicExchange {
        return TopicExchange("${EXCHANGE_NAME}.comment.like")
    }

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }
}
