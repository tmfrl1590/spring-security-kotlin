package com.example.springsecurity.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUser(
    val userId: Long,  // userId를 저장할 수 있는 프로퍼티
    userName: String,
    password: String,
    authorities: Collection<GrantedAuthority>
): User(userName, password, authorities)