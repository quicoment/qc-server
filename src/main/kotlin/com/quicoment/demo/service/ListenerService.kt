package com.quicoment.demo.service

import com.quicoment.demo.dto.QueueDelete
import com.quicoment.demo.dto.QueueRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "mq-client", url = "http://localhost:9090") // TODO: real url injection from property
interface ListenerService {
    @PostMapping("/queues")
    fun newQueueListener(requestBody: QueueRequest)

    @DeleteMapping("/queues")
    fun deleteQueueListener(requestBody: QueueDelete)
}
