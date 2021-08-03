package com.quicoment.demo.service

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
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
import java.util.*

@ExtendWith(MockitoExtension::class)
class PostServiceTest {
    private val post = Post(1, "title", "content", "password")

    @Test
    fun savePostTestSuccess() {
        // given
        val noIdPost = Post("title", "content", "password")
        val postRepository: PostRepository = mock { on { save(noIdPost) } doReturn post }

        // when
        val postService = PostService(postRepository)
        val result = postService.savePost(noIdPost)

        // then
        Mockito.verify(postRepository).save(noIdPost)
        assertEquals(1, result)
    }

    @Test
    @DisplayName("save post fail - post id is null")
    fun savePostTestFail1() {
        // given
        val noIdPost = Post("title", "content", "password")
        val postRepository: PostRepository = mock { on { save(noIdPost) } doReturn noIdPost }

        // when
        val postService = PostService(postRepository)

        // then
        val exception = assertThrows(FailCreateResourceException::class.java) {
            postService.savePost(noIdPost)
        }
        Mockito.verify(postRepository).save(noIdPost)
        assertEquals(exception.message, ErrorCase.SAVE_POST_FAIL.getMessage())
    }

    @Test
    fun findAllPostsTestSuccess1() {
        // given
        val posts = listOf(Post("title1", "content1", "password1"), Post("title2", "content2", "password2"))
        val postRepository: PostRepository = mock { on { findAll() } doReturn posts }

        // when
        val postService = PostService(postRepository)
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
        val postService = PostService(postRepository)
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
        val postService = PostService(postRepository)
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
            on { findById(100) } doReturn Optional.empty()
        }

        // when
        val postService = PostService(postRepository)

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
            on { findById(anyLong()) } doThrow IllegalArgumentException("post id must not be null")
        }

        // when
        val postService = PostService(postRepository)

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
        val postService = PostService(postRepository)
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
        val postService = PostService(postRepository)

        // then
        val exception = assertThrows(NoSuchResourceException::class.java) {
            postService.updatePost(100, "new_title", "new_content", "new_password")
        }
        Mockito.verify(postRepository).findById(100)
        assertEquals(exception.message, ErrorCase.NO_SUCH_POST.getMessage())
    }

    @Test
    fun deletePostTestSuccess() {
        // given
        val postRepository: PostRepository = mock { on { findById(1) } doReturn Optional.of(post) }
        doNothing().`when`(postRepository).delete(post)

        // when
        val postService = PostService(postRepository)
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
        val postService = PostService(postRepository)

        // then
        val exception = assertThrows(NoSuchResourceException::class.java) {
            postService.deletePost(1)
        }
        Mockito.verify(postRepository).findById(1)
        assertEquals(exception.message, ErrorCase.NO_SUCH_POST.getMessage())
    }
}
