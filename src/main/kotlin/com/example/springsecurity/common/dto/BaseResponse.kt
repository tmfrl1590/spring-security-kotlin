package com.example.springsecurity.common.dto

import com.example.springsecurity.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.msg
)