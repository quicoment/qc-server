package com.quicoment.demo.domain

import lombok.Getter
import lombok.Setter
import javax.persistence.MappedSuperclass
import java.time.LocalDateTime

import org.hibernate.annotations.UpdateTimestamp

import org.hibernate.annotations.CreationTimestamp
import javax.persistence.Column

@Getter
@Setter
@MappedSuperclass
open class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private val createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    private val updatedAt: LocalDateTime? = null
}
