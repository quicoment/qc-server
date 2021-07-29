package com.quicoment.demo.dto

data class QueueRequest(val QueueName: String, val DirectRoutingKey: String, val TopicRoutingKey: String)
