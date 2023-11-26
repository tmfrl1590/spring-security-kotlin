package com.example.springsecurity.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean(){

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val token = resolveToken(request as HttpServletRequest)

        if(token != null && jwtTokenProvider.validateToken(token)){ // validateToken 통과하고 값이 있으면 정상적인 토큰
            val authentication = jwtTokenProvider.getAuthentication(token) // 토큰안의 정보를 뽑아옴
            SecurityContextHolder.getContext().authentication = authentication  // SecurityContextHolder 안에 기록하게 됨
        }

        chain?.doFilter(request, response)
    }

    // 헤더에서 Authorization 문자열을 가져와서 bearer 문자열이 맞으면 그 뒤의 키값만 뽑아옴
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            bearerToken.substring(7)
        }else{
            null
        }
    }

}