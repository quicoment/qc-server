package com.quicoment.demo.dto.comment

import com.quicoment.demo.util.TimestampUtil
import java.io.Serializable

data class CommentRegister(val postId: Long?, val content: String?, val password: String?) : Serializable {
    val messageType = "register"
    val timestamp = TimestampUtil.currentDatetime()
}
