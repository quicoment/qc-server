package com.quicoment.demo

import com.quicoment.demo.config.RabbitConfigurationTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [RabbitConfigurationTest::class])
class DemoApplicationTests {

    @Test
    fun contextLoads() {
    }

}
