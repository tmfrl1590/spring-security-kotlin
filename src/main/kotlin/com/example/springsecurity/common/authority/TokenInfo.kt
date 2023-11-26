package com.example.springsecurity.common.authority

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
)
