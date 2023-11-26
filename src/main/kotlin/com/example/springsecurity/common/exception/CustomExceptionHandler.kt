package com.example.springsecurity.common.exception

import com.example.springsecurity.common.dto.BaseResponse
import com.example.springsecurity.common.status.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice // 응답으로 객체를 리턴
class CustomExceptionHandler {

    // ExceptionHandler : 이 어노테이션을 메서드에 선언하고 특정 예외 클래스를 지정해주면 해당 예외가 발생했을 때 메서드에 정의한 로직으로 처리

    @ExceptionHandler(MethodArgumentNotValidException::class)   // DTO 에 추가한 validation에서 exception이 발생하면 MethodArgumentNotValidException 을 떨어뜨리게함
    // 그 때 exception을 받아와서 처리함
    protected fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Map<String, String>>>{
        println("methodArgumentNotValidException")
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->    // exception에 들어있는 모든 에러들을 가져와서 필드이름과 에러메세지를 mutable map에 담음
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }

        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException): ResponseEntity<BaseResponse<Map<String, String>>>{
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        println("invalidInputException")
        println("ex.fieldName : ${ex.fieldName}")
        println("ex.message : ${ex.message}")
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

    // 로그인시 아이디 비번 값이 잘못됐을때?
    @ExceptionHandler(BadCredentialsException::class)
    protected fun badCredentialsException(ex: BadCredentialsException): ResponseEntity<BaseResponse<Map<String, String>>>{
        println("BadCredentialsException")
        val errors = mapOf("로그인 실패" to "아이디 혹은 비밀번호를 다시 확인하세요.")
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception): ResponseEntity<BaseResponse<Map<String, String>>>{
        println("defaultException")
        val errors = mapOf("미처리 예외" to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, errors, ResultCode.ERROR.msg), HttpStatus.BAD_REQUEST)
    }
}