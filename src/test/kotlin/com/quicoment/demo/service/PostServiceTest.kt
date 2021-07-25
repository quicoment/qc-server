package com.quicoment.demo.service

import com.nhaarman.mockitokotlin2.*
import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.repository.PostRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import java.util.*

@ExtendWith(MockitoExtension::class)
class PostServiceTest {
    private val mockRabbitAdmin: RabbitAdmin = mock()
    private val mockCommentRegisterExchange: DirectExchange = mock()
    private val mockCommentLikeExchange: TopicExchange = mock()
    private val queueDomain = "q.example"

    private val post = Post(1, "title", "content", "password")


    @Test
    fun savePostTestSuccess() {
        // given
        val postRepository: PostRepository = mock { on { save(post) } doReturn post }
        doReturn("${queueDomain}.post.${post.id}").`when`(mockRabbitAdmin).declareQueue(any())
        doNothing().`when`(mockRabbitAdmin).declareBinding(any())

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        val result = postService.savePost(post)

        // then
        Mockito.verify(postRepository).save(post)
        assertEquals(1, result)
    }

    @Test
    @DisplayName("save post fail - post id is null")
    fun savePostTestFail1() {
        // given
        val noIdPost = Post("title", "content", "password")
        val postRepository: PostRepository = mock { on { save(noIdPost) } doReturn noIdPost }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(FailCreateResourceException::class.java) {
            postService.savePost(noIdPost)
        }
        Mockito.verify(postRepository).save(noIdPost)
        assertEquals(exception.message, ErrorCase.SAVE_POST_FAIL.getMessage())
    }

    @Test
    @DisplayName("save post fail - fail declare queue")
    fun savePostTestFail2() {
        // given
        val postRepository: PostRepository = mock { on { save(post) } doReturn post }
        val rabbitAdmin: RabbitAdmin = mock {
            on { declareQueue(any()) } doThrow AmqpException("fail declare queue")
        }

        // when
        val postService = PostService(postRepository, rabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(AmqpException::class.java) {
            postService.savePost(post)
        }
        assertEquals(exception.message, "fail declare queue")
        Mockito.verify(postRepository).save(post)
    }

    @Test
    @DisplayName("save post fail - fail declare binding")
    fun savePostTestFail3() {
        // given
        val postRepository: PostRepository = mock { on { save(post) } doReturn post }
        val rabbitAdmin: RabbitAdmin = mock {
            on { declareBinding(any()) } doThrow AmqpException("fail declare binding")
        }
        doReturn("${queueDomain}.post.${post.id}").`when`(rabbitAdmin).declareQueue(any())

        // when
        val postService = PostService(postRepository, rabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(AmqpException::class.java) {
            postService.savePost(post)
        }
        assertEquals(exception.message, "fail declare binding")
        Mockito.verify(postRepository).save(post)
    }

    @Test
    fun findAllPostsTestSuccess1() {
        // given
        val posts = listOf(Post("title1", "content1", "password1"), Post("title2", "content2", "password2"))
        val postRepository: PostRepository = mock { on { findAll() } doReturn posts }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        val result = postService.findAllPosts()

        // then
        Mockito.verify(postRepository).findAll()
        assertEquals(2, result.size)
        assertEquals(posts.map { it.toResponseDto() }, result)
    }

    @Test
    @DisplayName("find all post success - all posts list is empty")
    fun findAllPostsTestSuccess2() {
        // given
        val postRepository: PostRepository = mock { on { findAll() } doReturn emptyList<Post>() }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        val result = postService.findAllPosts()

        // then
        Mockito.verify(postRepository).findAll()
        assertEquals(emptyList<Post>(), result)
    }

    @Test
    fun findPostByIdTestSuccess() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.of(post) }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        val result = postService.findPostById(1)

        // then
        Mockito.verify(postRepository).findById(1)
        assertEquals(post.toResponseDto(), result)
        assertEquals(1, result.id)
        assertEquals("title", result.title)
        assertEquals("content", result.content)
        assertEquals("password", result.password)
    }

    @Test
    @DisplayName("post findById fail - no such post id")
    fun findPostByIdTestFail1() {
        // given
        val postRepository: PostRepository = mock {
            on {
                findById(100)
            } doReturn Optional.empty()
        }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(NoSuchResourceException::class.java) {
            postService.findPostById(100)
        }
        Mockito.verify(postRepository).findById(100)
        assertEquals(exception.message, ErrorCase.NO_SUCH_POST.getMessage())
    }

    @Test
    @DisplayName("post findById fail - post id is null")
    fun findPostByIdTestFail2() {
        // given
        val postRepository: PostRepository = mock {
            on {
                findById(anyLong())
            } doThrow IllegalArgumentException("post id must not be null")
        }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            postService.findPostById(1)
        }
        Mockito.verify(postRepository).findById(1)
        assertEquals(exception.message, "post id must not be null")
    }

    @Test
    fun updatePostTestSuccess() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.of(post) }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        postService.updatePost(1, "new_title", "new_content", "new_password")

        // then
        Mockito.verify(postRepository).findById(1)
        assertEquals("new_title", post.title)
        assertEquals("new_content", post.content)
        assertEquals("new_password", post.password)
    }

    @Test
    @DisplayName("update post fail - no such post id")
    fun updatePostTestFail1() {
        // given
        val postRepository: PostRepository = mock {
            on { findById(100) } doReturn Optional.empty()
        }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(NoSuchResourceException::class.java) {
            postService.updatePost(100, "new_title", "new_content", "new_password")
        }
        Mockito.verify(postRepository).findById(100)
        assertEquals(exception.message, ErrorCase.NO_SUCH_POST.getMessage())
    }

    @Test
    fun deletePostTestSuccess1() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.of(post) }
        doNothing().`when`(postRepository).delete(post)

        val rabbitAdmin: RabbitAdmin = mock { on { deleteQueue(any()) } doReturn true }

        // when
        val postService = PostService(postRepository, rabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        postService.deletePost(1)

        // then
        Mockito.verify(postRepository).findById(1)
        Mockito.verify(postRepository).delete(post)
    }

    @Test
    @DisplayName("delete post success - no such queue")
    fun deletePostTestSuccess2() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.of(post) }
        doNothing().`when`(postRepository).delete(post)

        val rabbitAdmin: RabbitAdmin = mock { on { deleteQueue(any()) } doReturn false }

        // when
        val postService = PostService(postRepository, rabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)
        postService.deletePost(1)

        // then
        Mockito.verify(postRepository).findById(1)
        Mockito.verify(postRepository).delete(post)
    }

    @Test
    @DisplayName("delete post fail - no such post id")
    fun deletePostTestFail() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.empty() }

        // when
        val postService = PostService(postRepository, mockRabbitAdmin, mockCommentRegisterExchange, mockCommentLikeExchange, queueDomain)

        // then
        val exception = assertThrows(NoSuchResourceException::class.java) {
            postService.deletePost(1)
        }
        Mockito.verify(postRepository).findById(1)
        assertEquals(exception.message, ErrorCase.NO_SUCH_POST.getMessage())
    }
}
