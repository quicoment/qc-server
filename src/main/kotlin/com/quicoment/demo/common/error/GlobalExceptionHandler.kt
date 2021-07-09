package com.quicoment.demo.common.error

import com.quicoment.demo.common.ResultOf
import com.quicoment.demo.common.error.custom.InvalidFieldException
import com.quicoment.demo.common.error.custom.NoSuchPostException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidHTTPMethod(e: HttpRequestMethodNotSupportedException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.INVALID_METHOD.getCode(), ErrorCase.INVALID_METHOD.getMessage()))
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidJSONFormat(e: HttpMessageNotReadableException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.INVALID_FORMAT.getCode(), ErrorCase.INVALID_FORMAT.getMessage()))
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidPostNumber(e: MethodArgumentTypeMismatchException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.INVALID_TYPE.getCode(), ErrorCase.INVALID_TYPE.getMessage()))
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    fun handleNoSuchPost(e: NoSuchPostException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.NO_SUCH_POST.getCode(), ErrorCase.NO_SUCH_POST.getMessage()))
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    fun handleInvalidField(e: InvalidFieldException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.badRequest().body(ResultOf.Error(ErrorCase.INVALID_FIELD.getCode(), ErrorCase.INVALID_FIELD.getMessage()))
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleConnectionFail(e: HttpServerErrorException): ResponseEntity<ResultOf.Error> {
        return ResponseEntity.internalServerError().body(ResultOf.Error(ErrorCase.CONNECTION_FAIL.getCode(), ErrorCase.CONNECTION_FAIL.getMessage()))
    }
}
