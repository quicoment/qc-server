package com.quicoment.demo.config

import org.springframework.stereotype.Component
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import javax.annotation.PreDestroy

@Component
class RabbitComponent {
    @PreDestroy
    fun stop() {
        RABBITMQ_CONTAINER.stop()
    }

    companion object {
        private const val USERNAME = "guest"
        private const val PASSWORD = "guest"

        @Container
        @JvmStatic
        val RABBITMQ_CONTAINER = RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"))
            .apply {
                withUser(USERNAME, PASSWORD)
                start()
            }
    }
}
