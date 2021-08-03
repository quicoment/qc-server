package com.quicoment.demo.dto.comment

import com.quicoment.demo.util.TimestampUtil
import java.io.Serializable

data class CommentUpdate(val commentId: String?, val content: String?): Serializable {
    val messageType = "update"
    val timestamp = TimestampUtil.currentDatetime()
}
