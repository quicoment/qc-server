package com.quicoment.demo.repository

import com.quicoment.demo.common.error.ErrorCase
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import com.quicoment.demo.domain.Post
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.persistence.EntityManagerFactory


@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    companion object {
        @JvmStatic
        @Container
        private val MYSQL_CONTAINER = MySQLContainer<Nothing>("mysql:latest")
            .apply { withDatabaseName("quicoment") }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            MYSQL_CONTAINER.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            MYSQL_CONTAINER.stop()
        }
    }

    @Test
    @Order(1)
    fun savePostTestSuccess() {
        // given
        val em = entityManagerFactory.createEntityManager()
        val tx = em.transaction
        tx.begin()

        // when
        val savedPost = postRepository.save(Post(title = "title1", content = "content1", password = "password1"))

        tx.commit()
        em.close()

        // then
        assertAll(
            { assertEquals(1, savedPost.id) },
            { assertEquals("title1", savedPost.title) },
            { assertEquals("content1", savedPost.content) },
            { assertEquals("password1", savedPost.password) }
        )
    }

    @Test
    @Order(2)
    fun findAllPostsTestSuccess() {
        // given
        val em = entityManagerFactory.createEntityManager()
        val tx = em.transaction
        tx.begin()

        postRepository.save(Post(title = "title2", content = "content2", password = "password2"))
        postRepository.save(Post(title = "title3", content = "content3", password = "password3"))

        tx.commit()
        em.close()

        // when
        val savedPosts = postRepository.findAll()

        // then
        assertAll(
            { assertFalse(savedPosts.isEmpty()) },
            { assertEquals(2, savedPosts.size) },
            { assertNotNull(savedPosts[0].id) },
            { assertNotNull(savedPosts[1].id) }
        )
    }

    @Test
    @Order(3)
    fun findPostByIdTestSuccess() {
        // given
        val em = entityManagerFactory.createEntityManager()
        val tx = em.transaction
        tx.begin()

        postRepository.save(Post(title = "title4", content = "content4", password = "password4"))

        tx.commit()
        em.close()

        // when
        val savedPost = postRepository.findById(4)
            .orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }

        // then
        assertAll(
            { assertEquals(4, savedPost.id) },
            { assertEquals("title4", savedPost.title) },
            { assertEquals("content4", savedPost.content) },
            { assertEquals("password4", savedPost.password) }
        )
    }

    @Test
    @Order(4)
    fun updatePostTestSuccess() {
        // given
        val em = entityManagerFactory.createEntityManager()
        val tx = em.transaction
        tx.begin()

        postRepository.save(Post(title = "title5", content = "content5", password = "password5"))

        tx.commit()

        // when
        tx.begin()

        val savedPost = postRepository.findById(5)
            .orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }

        tx.commit()
        em.close()

        // then
        assertAll(
            { assertEquals(5, savedPost.id) },
            { assertEquals("title5", savedPost.title) },
            { assertEquals("content5", savedPost.content) },
            { assertEquals("password5", savedPost.password) }
        )
    }

    @Test
    @Order(5)
    fun deletePostTestSuccess() {
        // given
        val em = entityManagerFactory.createEntityManager()
        val tx = em.transaction
        tx.begin()

        val post6 = Post(title = "title6", content = "content6", password = "password6")
        postRepository.save(post6)

        tx.commit()

        // when
        tx.begin()

        postRepository.findById(6)
            .orElseThrow { NoSuchResourceException(ErrorCase.NO_SUCH_POST.getMessage()) }
            .let { postRepository.delete(it) }

        tx.commit()
        em.close()

        val savedPost = postRepository.findAll()

        // then
        assertEquals(emptyList<Post>(), savedPost)
    }
}
