package com.quicoment.demo.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostRequest
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import com.quicoment.demo.service.PostService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class)
@WebMvcTest(PostController::class)
class PostControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockBean
    private lateinit var postService: PostService

    @MockBean
    private lateinit var postRepository: PostRepository

    private val mapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun savePostTestSuccess() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")
        val postResponse = PostResponse(1, "title-example", "content-example", "password-example")
        val postEntity = Post("title-example", "content-example", "password-example")

        given(postService.savePost(postEntity)).willReturn(postResponse)

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            header { stringValues("Location", "/posts/1") }
        }
    }

    @DisplayName("필수 필드 누락")
    @Test
    fun savePostTestFail1() {
        val postRequest1 = PostRequest(null, "content-example", "password-example")
        val postRequest2 = PostRequest("title-example", null, "password-example")
        val postRequest3 = PostRequest("title-example", "content-example", null)

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest1)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(400) }
            jsonPath("\$.message") { value("필수항목을 입력해주세요. ") }
        }

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest2)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(400) }
            jsonPath("\$.message") { value("필수항목을 입력해주세요. ") }
        }

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest3)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(400) }
            jsonPath("\$.message") { value("필수항목을 입력해주세요. ") }
        }
    }

    @DisplayName("DB에서 가져온 값에 id 누락")
    @Test
    fun savePostTestFail2() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")
        val postResponse = PostResponse(null,"title-example", "content-example", "password-example")
        val postEntity = Post("title-example", "content-example", "password-example")

        given(postService.savePost(postEntity)).willReturn(postResponse)

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(500) }
            jsonPath("\$.message") { value("DB 및 서버에 오류가 있습니다. 잠시 후 다시 시도해 주세요. ") }
        }
    }
}
