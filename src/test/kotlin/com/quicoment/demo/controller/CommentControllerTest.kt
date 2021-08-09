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
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(CommentController::class)
internal class CommentControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockBean
    private lateinit var commentService: CommentService

    private val mapper = jacksonObjectMapper()
    private val postId: Long = 1
    private val commentId = "qu1c0m3n-t777-1886-2021-qu1c0m3t1886"

    @Test
    fun registerCommentTestSuccess() {
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

    @DisplayName("register comment - 필수 필드 누락")
    @Test
    fun registerCommentTestFail1() {
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

    @DisplayName("register comment - convertAndSend exception")
    @Test
    fun registerCommentTestFail2() {
        val commentRequest = CommentRegisterRequest(content = "content", password = "password")

        given(commentService.registerComment(commentRequest.toRegisterDto(postId)))
            .willThrow(AmqpException("there is a problem"))

        mockMvc.post("/posts/${postId}/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(ErrorCase.CONNECTION_FAIL.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }

    @DisplayName("잘못된 path variable type")
    @Test
    fun registerCommentTestFail3() {
        val commentRequest = CommentRegisterRequest(content = "content", password = "password")

        mockMvc.post("/posts/character/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_TYPE.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_TYPE.getMessage()) }
        }
    }

    @DisplayName("Invalid Content-Type header")
    @Test
    fun registerCommentTestFail4() {
        val commentRequest = CommentRegisterRequest(content = "content", password = "password")

        mockMvc.post("/posts/${postId}/comments") {
            content = mapper.writeValueAsString(commentRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnsupportedMediaType() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_HEADER.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_HEADER.getMessage()) }
        }
    }

    @Test
    fun likeCommentTestSuccess() {
        val commentLikeRequest = CommentLike(postId, commentId, "user-id")

        doNothing().`when`(commentService).likeComment(commentLikeRequest)

        mockMvc.patch("/posts/${postId}/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentLikeRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @DisplayName("like comment - 필수 필드 누락")
    @Test
    fun likeCommentTestFail1() {
        val commentLikeRequest = CommentLike(postId, commentId, null)

        mockMvc.patch("/posts/${postId}/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentLikeRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("like comment - convertAndSend exception")
    @Test
    fun likeCommentTestFail2() {
        val commentLikeRequest = CommentLike(postId, commentId, "user-id")

        given(commentService.likeComment(commentLikeRequest)).willThrow(AmqpException("there is a problem"))

        mockMvc.patch("/posts/${postId}/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentLikeRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(ErrorCase.CONNECTION_FAIL.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }

    @DisplayName("잘못된 path variable type")
    @Test
    fun likeCommentTestFail3() {
        val commentLikeRequest = CommentLike(postId, commentId, "user-id")

        mockMvc.patch("/posts/character/comments/${commentId}/like") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(commentLikeRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_TYPE.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_TYPE.getMessage()) }
        }
    }

    @DisplayName("Invalid Content-Type header")
    @Test
    fun likeCommentTestFail4() {
        val commentLikeRequest = CommentLike(postId, commentId, "user-id")

        mockMvc.patch("/posts/${postId}/comments/${commentId}/like") {
            content = mapper.writeValueAsString(commentLikeRequest)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnsupportedMediaType() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_HEADER.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_HEADER.getMessage()) }
        }
    }

    @Test
    fun updateCommentTestSuccess() {
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        doNothing().`when`(commentService).updateComment(newComment.toUpdateDto(postId, commentId))

        mockMvc.put("/posts/${postId}/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }
    }

    @DisplayName("update comment - 필수 필드 누락")
    @Test
    fun updateCommentTestFail1() {
        val newComment1 = CommentUpdateRequest(password = null, content = "content")
        val newComment2 = CommentUpdateRequest(password = "password", content = null)

        mockMvc.put("/posts/${postId}/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment1)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.put("/posts/${postId}/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment2)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("update comment - convertAndSend exception")
    @Test
    fun updateCommentTestFail2() {
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        given(commentService.updateComment(newComment.toUpdateDto(postId, commentId)))
            .willThrow(AmqpException("there is a problem"))

        mockMvc.put("/posts/${postId}/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(ErrorCase.CONNECTION_FAIL.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.CONNECTION_FAIL.getMessage()) }
        }
    }

    @DisplayName("잘못된 path variable type")
    @Test
    fun updateCommentTestFail3() {
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        mockMvc.put("/posts/character/comments/${commentId}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_TYPE.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_TYPE.getMessage()) }
        }
    }

    @DisplayName("Invalid Content-Type header")
    @Test
    fun updateCommentTestFail4() {
        val newComment = CommentUpdateRequest(password = "password", content = "content")

        mockMvc.put("/posts/${postId}/comments/${commentId}") {
            content = mapper.writeValueAsString(newComment)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnsupportedMediaType() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_HEADER.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_HEADER.getMessage()) }
        }
    }
}
