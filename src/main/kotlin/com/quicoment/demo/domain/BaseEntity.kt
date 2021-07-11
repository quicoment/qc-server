package com.quicoment.demo.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity {
    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    open val createAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "update_at")
    open var updateAt: LocalDateTime? = null
}
