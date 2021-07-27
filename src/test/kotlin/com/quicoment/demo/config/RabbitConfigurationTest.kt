package com.quicoment.demo.config

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConfigurationTest {
    @Bean
    fun connectionFactoryTest(): ConnectionFactory = CachingConnectionFactory(
        RabbitComponent.RABBITMQ_CONTAINER.host,
        RabbitComponent.RABBITMQ_CONTAINER.amqpPort)
        .apply {
            username = RabbitComponent.RABBITMQ_CONTAINER.adminUsername
            setPassword(RabbitComponent.RABBITMQ_CONTAINER.adminPassword)
        }

    @Bean
    fun rabbitAdminTest(connectionFactoryTest: ConnectionFactory): AmqpAdmin = RabbitAdmin(connectionFactoryTest)

    @Bean
    fun rabbitTemplateTest(connectionFactoryTest: ConnectionFactory): RabbitTemplate = RabbitTemplate(connectionFactoryTest)

    @Bean
    fun commentRegisterExchangeTest(): DirectExchange = DirectExchange("q.example.comment.register")

    @Bean
    fun commentLikeExchangeTest(): DirectExchange = DirectExchange("q.example.comment.register")

    @Bean
    fun messageConverterTest(): MessageConverter = Jackson2JsonMessageConverter()
}
