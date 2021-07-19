package com.quicoment.demo.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
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
    @Value("\${rabbitmq.queue-name}") private val QUEUE_NAME: String,
    @Value("\${rabbitmq.exchange-name}") private val EXCHANGE_NAME: String,
    @Value("\${rabbitmq.routing-key}") private val ROUTING_KEY: String
) {

    @Bean
    fun getConnectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(RABBITMQ_HOST, RABBITMQ_PORT.toInt())
        connectionFactory.username = USERNAME
        connectionFactory.setPassword(PASSWORD)
        return connectionFactory
    }

    @Bean
    fun getExchange(): TopicExchange {
        return TopicExchange(EXCHANGE_NAME)
    }

    @Bean
    fun defaultQueue(): Queue {
        return Queue(QUEUE_NAME)
    }

    @Bean
    fun defaultTopicExchange(): TopicExchange {
        return TopicExchange(EXCHANGE_NAME)
    }

    @Bean
    fun defaultBinding(defaultQueue: Queue, defaultTopicExchange: TopicExchange): Binding {
        return BindingBuilder.bind(defaultQueue).to(defaultTopicExchange).with(ROUTING_KEY)
    }
}
