package com.quicoment.demo.service

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.TopicExchange

@ExtendWith(MockitoExtension::class)
class MQServiceTest {
    private val mockRabbitAdmin: AmqpAdmin = mock()
    private val mockRegisterExchange: DirectExchange = mock()
    private val mockLikeExchange: TopicExchange = mock()
    private val queueDomain = "q.example"

    private val id = "1"

    @Test
    fun declarePostQueueTestSuccess() {
        // given
        doReturn("${queueDomain}.post.${id}").`when`(mockRabbitAdmin).declareQueue(any())
        doNothing().`when`(mockRabbitAdmin).declareBinding(any())

        // when
        val mqService = MQService(mockRabbitAdmin, mockRegisterExchange, mockLikeExchange, queueDomain)
        mqService.declarePostQueue(id)

        // then
        Mockito.verify(mockRabbitAdmin).declareQueue(any())
        Mockito.verify(mockRabbitAdmin, times(2)).declareBinding(any())
    }

    @Test
    @DisplayName("fail declare queue")
    fun declarePostQueueTestFail1() {
        // given
        doThrow(AmqpException("fail declare queue")).`when`(mockRabbitAdmin).declareQueue(any())

        // when
        val mqService = MQService(mockRabbitAdmin, mockRegisterExchange, mockLikeExchange, queueDomain)

        // then
        val exception = Assertions.assertThrows(AmqpException::class.java) {
            mqService.declarePostQueue(id)
        }
        assertEquals(exception.message, "fail declare queue")
    }

    @Test
    @DisplayName("fail declare binding")
    fun declarePostQueueTestFail2() {
        // given
        doReturn("${queueDomain}.post.${id}").`when`(mockRabbitAdmin).declareQueue(any())
        doThrow(AmqpException("fail declare binding")).`when`(mockRabbitAdmin).declareBinding(any())

        // when
        val mqService = MQService(mockRabbitAdmin, mockRegisterExchange, mockLikeExchange, queueDomain)

        // then
        val exception = Assertions.assertThrows(AmqpException::class.java) {
            mqService.declarePostQueue(id)
        }
        assertEquals(exception.message, "fail declare binding")
        Mockito.verify(mockRabbitAdmin).declareQueue(any())
    }

    @Test
    fun deletePostQueueTestSuccess1() {
        // given
        val queueName = "${queueDomain}.post.${id}"
        doReturn(true).`when`(mockRabbitAdmin).deleteQueue(queueName)

        // when
        val mqService = MQService(mockRabbitAdmin, mockRegisterExchange, mockLikeExchange, queueDomain)
        val result = mqService.deletePostQueue(id)

        // then
        Mockito.verify(mockRabbitAdmin).deleteQueue(queueName)
        assertEquals(true, result)
    }

    @Test
    @DisplayName("delete post success - no such queue")
    fun deletePostQueueTestSuccess2() {
        // given
        val queueName = "${queueDomain}.post.${id}"
        doReturn(false).`when`(mockRabbitAdmin).deleteQueue(queueName)

        // when
        val mqService = MQService(mockRabbitAdmin, mockRegisterExchange, mockLikeExchange, queueDomain)
        val result = mqService.deletePostQueue(id)

        // then
        Mockito.verify(mockRabbitAdmin).deleteQueue(queueName)
        assertEquals(false, result)
    }
}
