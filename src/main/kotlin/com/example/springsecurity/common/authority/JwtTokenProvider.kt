package com.example.springsecurity.common.authority

import com.example.springsecurity.common.dto.CustomUser
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 30 // 30분

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    // 토큰생성
    fun createToken(authentication: Authentication): TokenInfo {
        val authorities: String = authentication
            .authorities.joinToString(",", transform = GrantedAuthority::getAuthority)    // 권한들이 들어있는데  권한을 ,을 기준으로 스트링으로 쭉 뽑음

        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        // Access Token
        val accessToken = Jwts
            .builder()
            .setSubject(authentication.name)
            .claim("auth", authorities)   // auth 라는 이름으로 권한들을 담음
            .claim("userId", (authentication.principal as CustomUser).userId) // 토큰 생성할 때 클래임에 유저 아이디도 저장
            .setIssuedAt(now) // 토큰 발행시간
            .setExpiration(accessExpiration) // 언제까지 유효한지
            .signWith(key, SignatureAlgorithm.HS256) // 어떤 알고리즘을 썼는지
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    // 토큰 정보 추출( token : accessToken을 말함)
    fun getAuthentication(token: String): Authentication{
        val claims: Claims = getClaims(token) // 토큰안에서 클래임을 꺼내옴

        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.") // 클래임안에 auth를 뽑아오는데 값이 없으면 잘못된 토큰
        val userId = claims["userId"] ?: throw RuntimeException("잘못된 토큰입니다.")

        // 권한 정보 추출
        val authorities: Collection<GrantedAuthority> = (auth as String).split(",").map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = CustomUser(userId.toString().toLong() ,claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }


    /**
     * Token 검증
     */
    fun validateToken(token: String): Boolean {
        try {
            getClaims(token) // getClaims 을 호출하고 그 과정에서 익셉션이 발생하면 아래 익셉션으로 타게 됨
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is UnsupportedJwtException -> {} // Unsupported JWT Token
                is IllegalArgumentException -> {} // JWT claims string is empty
                else -> {} // else
            }
            println(e.message)
        }
        return false
    }


    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder().setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

}