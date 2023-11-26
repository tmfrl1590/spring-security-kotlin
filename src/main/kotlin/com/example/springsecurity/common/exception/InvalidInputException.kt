package com.example.springsecurity.common.exception

class InvalidInputException(
    val fieldName: String = "",
    message: String = "Invalid Input",
): RuntimeException(message)