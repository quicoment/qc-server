package com.quicoment.demo.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private val createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    private val updatedAt: LocalDateTime? = null
}
