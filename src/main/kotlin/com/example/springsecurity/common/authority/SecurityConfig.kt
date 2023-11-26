package com.example.springsecurity.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .csrf { it.disable()}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)} // jwt를 사용하기 때문에 Session을 사용하지 않는다고 설정
            .authorizeHttpRequests {  // 이 안에 권한 관리를 넣어줌
                it.requestMatchers("/api/member/signup", "/api/member/login").anonymous()  // 이 url을 호출하는 사람은 인증되지 않은 사용자여야  / 그 외의 요청은 아무 권한 없이 모두가 접근이 가능함
                    .requestMatchers("/api/member/**").hasRole("MEMBER") // 그 외의 api 호출은 MEMBER 권한이 있어야 호출이 가능하다.
                    .anyRequest().permitAll()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider), // JwtAuthenticationFilter 필터가  UsernamePasswordAuthenticationFilter필터보다 먼저 실행되라고 / 앞에 필터가 통과되면 뒤에 필터는 실행하지 않음
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    // 암호화
    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}