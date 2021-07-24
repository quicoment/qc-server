package com.quicoment.demo.repository

import com.quicoment.demo.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>
