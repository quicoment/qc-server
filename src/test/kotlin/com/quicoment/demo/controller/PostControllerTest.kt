package com.quicoment.demo.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.post.PostRequest
import com.quicoment.demo.dto.post.PostResponse
import com.quicoment.demo.service.PostService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*


@ExtendWith(SpringExtension::class)
@WebMvcTest(PostController::class)
internal class PostControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockBean
    private lateinit var postService: PostService

    private val mapper = jacksonObjectMapper()

    @Test
    fun savePostTestSuccess() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")
        val postEntity = Post("title-example", "content-example", "password-example")

        given(postService.savePost(postEntity)).willReturn(1)

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
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest2)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }.andDo { print() }

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest3)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("DB에서 가져온 Post에 id값 없을 때")
    @Test
    fun savePostTestFail2() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")
        val postEntity = Post("title-example", "content-example", "password-example")

        given(postService.savePost(postEntity)).willThrow(FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage()))

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(ErrorCase.SAVE_POST_FAIL.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.SAVE_POST_FAIL.getMessage()) }
        }
    }

    @DisplayName("save 중 runtime exception")
    @Test
    fun savePostTestFail3() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")
        val postEntity = Post("title-example", "content-example", "password-example")

        given(postService.savePost(postEntity)).willThrow(IllegalArgumentException("fail save null entity"))

        mockMvc.post("/posts") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value("fail save null entity") }
        }
    }

    @DisplayName("Invalid Content-Type header")
    @Test
    fun savePostTestFail4() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")

        mockMvc.post("/posts") {
            content = mapper.writeValueAsString(postRequest)
        }.andExpect {
            status { isUnsupportedMediaType() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_HEADER.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_HEADER.getMessage()) }
        }
    }

    @Test
    fun findAllPostsTestSuccess() {
        val posts = listOf(
            PostResponse(1, "title-example-1", "content-example-1", "password-example-1"),
            PostResponse(2, "title-example-2", "content-example-2", "password-example-2")
        )

        given(postService.findAllPosts()).willReturn(posts)

        mockMvc.get("/posts") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.data.length()") { value(posts.size) }
            jsonPath("\$.data[0].id") { value(posts[0].id) }
            jsonPath("\$.data[0].title") { value(posts[0].title) }
            jsonPath("\$.data[0].content") { value(posts[0].content) }
            jsonPath("\$.data[0].password") { value(posts[0].password) }
        }
    }

    @Test
    fun findPostByIdTestSuccess() {
        val postResponse = PostResponse(1, "title-example-1", "content-example-1", "password-example-1")
        given(postService.findPostById(1)).willReturn(postResponse)

        mockMvc.get("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("\$.data.id") { value(postResponse.id) }
            jsonPath("\$.data.title") { value(postResponse.title) }
            jsonPath("\$.data.content") { value(postResponse.content) }
            jsonPath("\$.data.password") { value(postResponse.password) }
        }
    }

    @DisplayName("잘못된 path variable type")
    @Test
    fun findPostByIdTestFail1() {
        mockMvc.get("/posts/character") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_TYPE.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_TYPE.getMessage()) }
        }
    }

    @DisplayName("없는 post id")
    @Test
    fun findPostByIdTestFail2() {
        given(postService.findPostById(1)).willThrow(NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()))

        mockMvc.get("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("\$.code") { value(ErrorCase.NO_SUCH_POST.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.NO_SUCH_POST.getMessage()) }
        }
    }

    @DisplayName("id가 null이 되서 JPA findById에서 exception throw")
    @Test
    fun findPostByIdTestFail3() {
        given(postService.findPostById(1)).willThrow(IllegalArgumentException("The given id must not be null!"))

        mockMvc.get("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value("The given id must not be null!") }
        }
    }

    @Test
    fun updatePostSuccess() {
        val postRequest = PostRequest("new-title", "new-content", "new-password")
        doNothing().`when`(postService).updatePost(1, "new-title", "new-content", "new-password")

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("필수 필드 누락")
    @Test
    fun updatePostFail1() {
        val postRequest1 = PostRequest(null, "new-content", "new-password")
        val postRequest2 = PostRequest("new-title", null, "new-password")
        val postRequest3 = PostRequest("new-title", "new-content", null)

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest1)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest2)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest3)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_FIELD.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_FIELD.getMessage()) }
        }
    }

    @DisplayName("없는 post id")
    @Test
    fun updatePostFail2() {
        val postRequest = PostRequest("new-title", "new-content", "new-password")
        given(postService.updatePost(1, "new-title", "new-content", "new-password"))
            .willThrow(NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()))

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("\$.code") { value(ErrorCase.NO_SUCH_POST.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.NO_SUCH_POST.getMessage()) }
        }
    }

    @DisplayName("id가 null이 되서 JPA findById에서 exception throw")
    @Test
    fun updatePostFail3() {
        val postRequest = PostRequest("new-title", "new-content", "new-password")
        given(postService.updatePost(1, "new-title", "new-content", "new-password"))
            .willThrow(IllegalArgumentException("The given id must not be null!"))

        mockMvc.put("/posts/1") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(postRequest)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value("The given id must not be null!") }
        }
    }

    @DisplayName("Invalid Content-Type header")
    @Test
    fun updatePostTestFail4() {
        val postRequest = PostRequest("title-example", "content-example", "password-example")

        mockMvc.put("/posts/1") {
            content = mapper.writeValueAsString(postRequest)
        }.andExpect {
            status { isUnsupportedMediaType() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_HEADER.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_HEADER.getMessage()) }
        }
    }

    @Test
    fun deletePostSuccess() {
        doNothing().`when`(postService).deletePost(1)

        mockMvc.delete("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @DisplayName("없는 post id")
    @Test
    fun deletePostFail1() {
        given(postService.deletePost(1)).willThrow(NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()))

        mockMvc.delete("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("\$.code") { value(ErrorCase.NO_SUCH_POST.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.NO_SUCH_POST.getMessage()) }
        }
    }

    @DisplayName("id가 null이 되서 JPA findById에서 exception throw")
    @Test
    fun deletePostFail2() {
        given(postService.deletePost(1)).willThrow(IllegalArgumentException("The given id must not be null!"))

        mockMvc.delete("/posts/1") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isInternalServerError() }
            jsonPath("\$.code") { value(HttpStatus.INTERNAL_SERVER_ERROR.value()) }
            jsonPath("\$.message") { value("The given id must not be null!") }
        }
    }

    @DisplayName("잘못된 path variable type")
    @Test
    fun deletePostFail3() {
        mockMvc.delete("/posts/character") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("\$.code") { value(ErrorCase.INVALID_TYPE.getCode()) }
            jsonPath("\$.message") { value(ErrorCase.INVALID_TYPE.getMessage()) }
        }
    }
}
