package com.quicoment.demo.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockitokotlin2.doNothing
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.dto.comment.CommentLike
import com.quicoment.demo.dto.comment.CommentRegisterRequest
import com.quicoment.demo.dto.comment.CommentUpdateRequest
import com.quicoment.demo.service.CommentService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.amqp.AmqpException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(CommentController::class)
internal class CommentControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockBean
    private lateinit var commentService: CommentService

    private val mapper = jacksonObjectMapper()

    @Test
    fun registerCommentTestSuccess() {
        val postId: Long = 1
        val commentRequest = CommentRegisterRequest(content = "content", password = "password")

        doNothing().`when`(commentService).registerComment(commentRequest.toRegisterDto(postId))

        mockMvc.post("/posts/${postId}/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @DisplayName("필수 필드 누락")
    @Test
    fun registerCommentTestFail1() {
        val postId: Long = 1
        val commentRequest1 = CommentRegisterRequest(content = null, password = "password")
        val commentRequest2 = CommentRegisterRequest(content = "content", password = null)

        mockMvc.post("/posts/${postId}/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest1)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.post("/posts/${postId}/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest2)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("convertAndSend exception")
    @Test
    fun registerCommentTestFail2() {
        val postId: Long = 1
        val commentRequest = CommentRegisterRequest(content = "content", password = "password")

        given(commentService.registerComment(commentRequest.toRegisterDto(postId)))
            .willThrow(AmqpException("there is a problem"))

        mockMvc.post("/posts/${postId}/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }

    @Test
    fun likeCommentTestSuccess() {
        val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"
        val commentLike = CommentLike(commentId)

        doNothing().`when`(commentService).likeComment(commentLike)

        mockMvc.patch("/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @DisplayName("convertAndSend exception")
    @Test
    fun likeCommentTestFail() {
        val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"
        val commentLike = CommentLike(commentId)

        given(commentService.likeComment(commentLike)).willThrow(AmqpException("there is a problem"))

        mockMvc.patch("/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }

    @Test
    fun updateCommentTestSuccess() {
        val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        doNothing().`when`(commentService).updateComment(newComment.toUpdateDto(commentId))

        mockMvc.put("/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @DisplayName("필수 필드 누락")
    @Test
    fun updateCommentTestFail1() {
        val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"
        val newComment1 = CommentUpdateRequest(password = null, content = "content")
        val newComment2 = CommentUpdateRequest(password = "password", content = null)

        mockMvc.put("/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment1)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.put("/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment2)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("convertAndSend exception")
    @Test
    fun updateCommentTestFail2() {
        val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        given(commentService.updateComment(newComment.toUpdateDto(commentId)))
            .willThrow(AmqpException("there is a problem"))

        mockMvc.put("/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }
}
