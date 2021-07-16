package com.quicoment.demo.service

import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import com.quicoment.demo.dto.PostResponse
import com.quicoment.demo.repository.PostRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ActiveProfiles("test")
@SqlGroup(
    Sql(scripts = ["/db/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = SqlConfig(encoding = "utf-8")),
    Sql(scripts = ["/db/teardown.sql"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = SqlConfig(encoding = "utf-8"))
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
        val savedPostId: Long = postService.savePost(post)

        //then
        val findPost: Post = postRepository.findById(savedPostId).get()
        assertAll(
            { assertEquals(savedPostId, findPost.id) },
            { assertEquals(post.title, findPost.title) },
            { assertEquals(post.content, findPost.content) },
            { assertEquals(post.password, findPost.password) }
        )
    }

    @DisplayName("post findAllPosts 성공 테스트")
    @Test
    fun findAllPostsTestSuccess() {
        System.out.println(postRepository.findAll())
        //when
        val posts: List<PostResponse> = postService.findAllPosts()

        //then
        assertTrue(posts.isNotEmpty())
    }

    @DisplayName("post findPostById 성공 테스트")
    @Test
    fun findPostByIdTestSuccess() {
        //when
        val findPost: PostResponse = postService.findPostById(1L)

        //then
        assertAll(
            { assertEquals(1L, findPost.id) }
        )
    }

    @DisplayName("post findById 실패 테스트 - post id 없음 오류")
    @Test
    fun findPostByIdTestFail() {
        assertThatThrownBy { postService.findPostById(100L) }.isInstanceOf(NoSuchResourceException::class.java)
    }

    @DisplayName("post update 성공 테스트")
    @Test
    fun updatePostTestSuccess() {
        //when
        postService.updatePost(1L, "title-example-changed", "content-example-changed", "password-example-changed")

        //then
        val findPost: Post = postRepository.findById(1L).get()
        assertAll(
            { assertEquals(1L, findPost.id) },
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
        //when
        postService.deletePost(1L)

        //then
        val findPost: Optional<Post> = postRepository.findById(1L)
        assertTrue(findPost.isEmpty)
    }

    @DisplayName("post delete 실패 테스트 - post id 없음 오류")
    @Test
    fun deletePostTestFail() {
        assertThatThrownBy { postService.deletePost(100L) }.isInstanceOf(NoSuchResourceException::class.java)
    }
}
