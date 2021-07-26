package com.quicoment.demo.domain

import com.quicoment.demo.dto.PostResponse
import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(var title: String, var content: String, var password: String) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    constructor(id: Long, title: String, content: String, password: String) : this(title, content, password) {
        this.id = id
    }

    fun update(newTitle: String, newContent: String, newPassword: String) {
        this.title = newTitle
        this.content = newContent
        this.password = newPassword
    }

    fun toResponseDto(): PostResponse {
        return PostResponse(id, title, content, password)
    }

    override fun toString(): String {
        return "Post(id=$id, title='$title', content='$content', password='$password')"
    }
}
