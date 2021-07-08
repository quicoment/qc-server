package com.quicoment.demo.domain

import javax.persistence.*

@Entity
@Table(name = "posts")
class Post(title: String, content: String, password: String) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
    private var title: String = title
    private var content: String = content
    private var password: String = password

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

    fun update(newTitle: String, newContent: String, newPassword: String) {
        this.title = newTitle
        this.content = newContent
        this.password = newPassword
    }
}
