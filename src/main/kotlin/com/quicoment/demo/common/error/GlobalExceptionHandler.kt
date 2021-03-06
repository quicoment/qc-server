package com.quicoment.demo.common.error

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.FailCreateResourceException
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.common.error.custom.NoSuchResourceException
import org.springframework.amqp.AmqpConnectException
import org.springframework.amqp.AmqpException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleInvalidHTTPMethod(e: HttpMediaTypeNotSupportedException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
            ResultOf.Error(ErrorCase.INVALID_HEADER.getCode(), ErrorCase.INVALID_HEADER.getMessage()))
    }

    @ExceptionHandler
    fun handleInvalidHTTPMethod(e: HttpRequestMethodNotSupportedException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(
            ResultOf.Error(ErrorCase.INVALID_METHOD.getCode(), ErrorCase.INVALID_METHOD.getMessage()))
    }

    @ExceptionHandler
    fun handleInvalidJSONFormat(e: HttpMessageNotReadableException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(
            ResultOf.Error(ErrorCase.INVALID_FORMAT.getCode(), ErrorCase.INVALID_FORMAT.getMessage()))
    }

    @ExceptionHandler
    fun handleInvalidPostNumber(e: MethodArgumentTypeMismatchException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(
            ResultOf.Error(ErrorCase.INVALID_TYPE.getCode(), ErrorCase.INVALID_TYPE.getMessage()))
    }

    @ExceptionHandler
    fun handleFailCreateResource(e: FailCreateResourceException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.internalServerError().body(
            ResultOf.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.message ?: "????????? ????????? ??????????????????. "))
    }

    @ExceptionHandler
    fun handleNoSuchResource(e: NoSuchResourceException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity(
            ResultOf.Error(HttpStatus.NOT_FOUND.value(), e.message ?: "???????????? ???????????? ????????????. "), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleInvalidField(e: InvalidFieldException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(
            ResultOf.Error(ErrorCase.INVALID_FIELD.getCode(), ErrorCase.INVALID_FIELD.getMessage()))
    }

    @ExceptionHandler
    fun handleConnectionFail(e: HttpServerErrorException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.internalServerError().body(
            ResultOf.Error(ErrorCase.CONNECTION_FAIL.getCode(), ErrorCase.CONNECTION_FAIL.getMessage()))
    }

    @ExceptionHandler
    fun handleAMQPFail(e: AmqpException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.internalServerError().body(
            ResultOf.Error(ErrorCase.CONNECTION_FAIL.getCode(), ErrorCase.CONNECTION_FAIL.getMessage()))
    }

    @ExceptionHandler
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.internalServerError().body(
            ResultOf.Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.message ?: "??? ??? ?????? ????????? ??????????????????. "))
    }
}
