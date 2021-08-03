package com.quicoment.demo.config

import org.springframework.amqp.core.*
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
    fun messageConverterTest(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplateTest(connectionFactoryTest: ConnectionFactory, messageConverterTest: MessageConverter): RabbitTemplate =
        RabbitTemplate(connectionFactoryTest).apply{ messageConverter = messageConverterTest }

    @Bean
    fun defaultQueueTest(): Queue = Queue("q.example.default")

    @Bean
    fun defaultExchangeTest(): DirectExchange = DirectExchange("e.example.default")

    @Bean
    fun defaultBindingTest(defaultQueueTest: Queue, defaultExchangeTest: DirectExchange): Binding =
        BindingBuilder.bind(defaultQueueTest).to(defaultExchangeTest).with("defaultKey")
}
