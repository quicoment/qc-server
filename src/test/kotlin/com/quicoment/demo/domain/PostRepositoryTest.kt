package com.quicoment.demo.domain

import com.quicoment.demo.repository.PostRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@ExtendWith(SpringExtension::class)
@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private lateinit var postRepository: PostRepository
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        val post = postRepository.save(Post("제목", "내용", "비밀번호"))
    }

    @DisplayName("객체 전체를 조회한다")
    @Test
    fun findAll() {
        entityManager.clear()

        val posts = postRepository.findAll()
        Assertions.assertNotNull(posts)
    }
}
