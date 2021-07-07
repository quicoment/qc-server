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
}
