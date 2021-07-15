package com.quicoment.demo.service

import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest
class PostServiceTest(
    @Autowired val postService: PostService,
    @Autowired val postRepository: PostRepository
) {

    @DisplayName("post save 성공 테스트")
    @Test
    fun savePostTestSuccess() {
        //given
        val post = Post("title-example", "content-example", "password-example")

        //when
        val postId: Long = postService.savePost(post)

        //then
        val findPost: Post = postRepository.findById(postId).get()
        assertAll(
            { assertEquals(post.id, findPost.id) },
            { assertEquals(post.title, findPost.title) },
            { assertEquals(post.content, findPost.content) },
            { assertEquals(post.password, findPost.password) }
        )
    }

    @DisplayName("post findAllPosts 성공 테스트")
    @Test
    fun findAllPostsTestSuccess() {
        //given
        val post = Post("title-example", "content-example", "password-example")
        val postId: Long? = postRepository.save(post).id

        //when
        val posts: List<PostResponse> = postService.findAllPosts()

        //then
        assertTrue(posts.isNotEmpty())
    }

    @DisplayName("post findPostById 성공 테스트")
    @Test
    fun findPostByIdTestSuccess() {
        //given
        val post = Post("title-example", "content-example", "password-example")
        val postId: Long? = postRepository.save(post).id

        //when
        val findPost: PostResponse? = postId?.let { postService.findPostById(it) }

        //then
        if (findPost != null) {
            assertAll(
                { assertEquals(postId, findPost.id) },
                { assertEquals(post.title, findPost.title) },
                { assertEquals(post.content, findPost.content) },
                { assertEquals(post.password, findPost.password) }
            )
        } else {
            assertTrue(false)
        }
    }

    @DisplayName("post findById 실패 테스트 - post id 없음 오류")
    @Test
    fun findPostByIdTestFail() {
        assertThatThrownBy { postService.findPostById(100L) }.isInstanceOf(NoSuchResourceException::class.java)
    }

    @DisplayName("post update 성공 테스트")
    @Test
    fun updatePostTestSuccess() {
        //given
        val post = Post("title-example", "content-example", "password-example")
        val postId: Long = postRepository.save(post).id ?: throw FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage())

        //when
        postService.updatePost(postId, "title-example-changed", "content-example-changed", "password-example-changed")

        //then
        val findPost: Post = postRepository.findById(postId).get()
        assertAll(
            { assertEquals(postId, findPost.id) },
            { assertEquals("title-example-changed", findPost.title) },
            { assertEquals("content-example-changed", findPost.content) },
            { assertEquals("password-example-changed", findPost.password) }
        )
    }

    @DisplayName("post update 실패 테스트 - post id 없음 오류")
    @Test
    fun updatePostTestFail() {
        assertThatThrownBy { postService.updatePost(100L, "title-example-changed", "content-example-changed", "password-example-changed") }.isInstanceOf(NoSuchResourceException::class.java)
    }

    @DisplayName("post delete 성공 테스트")
    @Test
    fun deletePostTestSuccess() {
        //given
        val post = Post("title-example", "content-example", "password-example")
        val postId: Long = postRepository.save(post).id ?: throw FailCreateResourceException(ErrorCase.SAVE_POST_FAIL.getMessage())

        //when
        postService.deletePost(postId)

        //then
        val findPost: Optional<Post> = postRepository.findById(postId)
        assertTrue(findPost.isEmpty)
    }

    @DisplayName("post delete 실패 테스트 - post id 없음 오류")
    @Test
    fun deletePostTestFail() {
        assertThatThrownBy { postService.deletePost(100L) }.isInstanceOf(EmptyResultDataAccessException::class.java)
    }
}
