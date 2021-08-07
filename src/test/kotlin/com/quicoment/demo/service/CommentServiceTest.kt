package com.quicoment.demo.service

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.dto.comment.CommentLike
import com.quicoment.demo.dto.comment.CommentRegister
import com.quicoment.demo.dto.comment.CommentUpdate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.DirectExchange

@ExtendWith(MockitoExtension::class)
internal class CommentServiceTest {
    private val mockCommentExchange: DirectExchange = mock { on { name } doReturn "e.mock" }
    private val postId: Long = 1
    private val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"

    @Test
    fun registerCommentTestSuccess() {
        // given
        val commentRegister = CommentRegister(postId, commentId, "password")

        val mockRabbitTemplate: AmqpTemplate = mock()
        doNothing().`when`(mockRabbitTemplate).convertAndSend("e.mock", "register", commentRegister)

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)
        commentService.registerComment(commentRegister)

        // then
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "register", commentRegister)
    }

    @Test
    @DisplayName("convertAndSend fail - there is a problem")
    fun registerCommentTestFail() {
        // given
        val commentRegister = CommentRegister(postId, commentId, "password")

        val mockRabbitTemplate: AmqpTemplate = mock {
            on { convertAndSend(mockCommentExchange.name, "register", commentRegister)
            } doThrow (AmqpException(ErrorCase.CONNECTION_FAIL.getMessage()))
        }

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)

        // then
        val exception = Assertions.assertThrows(AmqpException::class.java) {
            commentService.registerComment(commentRegister)
        }
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "register", commentRegister)
        Assertions.assertEquals(exception.message, ErrorCase.CONNECTION_FAIL.getMessage())
    }

    @Test
    fun likeCommentTestSuccess() {
        // given
        val commentLike = CommentLike(postId, commentId, "user-id")

        val mockRabbitTemplate: AmqpTemplate = mock()
        doNothing().`when`(mockRabbitTemplate).convertAndSend("e.mock", "like", commentLike)

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)
        commentService.likeComment(commentLike)

        // then
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "like", commentLike)
    }

    @Test
    @DisplayName("convertAndSend fail - there is a problem")
    fun likeCommentTestFail() {
        // given
        val commentLike = CommentLike(postId, commentId, "user-id")

        val mockRabbitTemplate: AmqpTemplate = mock {
            on { convertAndSend(mockCommentExchange.name, "like", commentLike)
            } doThrow (AmqpException(ErrorCase.CONNECTION_FAIL.getMessage()))
        }

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)

        // then
        val exception = Assertions.assertThrows(AmqpException::class.java) {
            commentService.likeComment(commentLike)
        }
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "like", commentLike)
        Assertions.assertEquals(exception.message, ErrorCase.CONNECTION_FAIL.getMessage())
    }

    @Test
    fun updateCommentTestSuccess() {
        // given
        val commentUpdate = CommentUpdate(postId, commentId, "content")

        val mockRabbitTemplate: AmqpTemplate = mock()
        doNothing().`when`(mockRabbitTemplate).convertAndSend("e.mock", "update", commentUpdate)

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)
        commentService.updateComment(commentUpdate)

        // then
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "update", commentUpdate)
    }

    @Test
    @DisplayName("convertAndSend fail - there is a problem")
    fun updateCommentTestFail() {
        // given
        val commentUpdate = CommentUpdate(postId, commentId, "content")

        val mockRabbitTemplate: AmqpTemplate = mock {
            on { convertAndSend(mockCommentExchange.name, "update", commentUpdate)
            } doThrow (AmqpException(ErrorCase.CONNECTION_FAIL.getMessage()))
        }

        // when
        val commentService = CommentService(mockRabbitTemplate, mockCommentExchange)

        // then
        val exception = Assertions.assertThrows(AmqpException::class.java) {
            commentService.updateComment(commentUpdate)
        }
        Mockito.verify(mockRabbitTemplate).convertAndSend(mockCommentExchange.name, "update", commentUpdate)
        Assertions.assertEquals(exception.message, ErrorCase.CONNECTION_FAIL.getMessage())
    }
}
