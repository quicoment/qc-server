package com.quicoment.demo.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TimestampUtil {
    companion object {
        fun currentDatetime(): String = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
    }
}
