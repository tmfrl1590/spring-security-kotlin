package com.example.springsecurity.member.controller

import com.example.springsecurity.common.authority.TokenInfo
import com.example.springsecurity.common.dto.BaseResponse
import com.example.springsecurity.common.dto.CustomUser
import com.example.springsecurity.member.dto.LoginDto
import com.example.springsecurity.member.dto.MemberDtoRequest
import com.example.springsecurity.member.dto.MemberDtoResponse
import com.example.springsecurity.member.entity.Member
import com.example.springsecurity.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RequestMapping("/api/member")
@RestController
class MemberController(
    private val memberService: MemberService
) {

    // 회원가입
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Member> {
        //val resultMsg: String = memberService.signUp(memberDtoRequest)
        val resultMsg: Member = memberService.signUp(memberDtoRequest)
        return BaseResponse(
            message = "회원가입이 완료되었습니다.",
            data = resultMsg
        )
    }

    // 로그인
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }

    // 내정보 조회
    @GetMapping("/info")
    fun searchMyInfo(userId: Long): BaseResponse<MemberDtoResponse>{
        //val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        val response = memberService.searchMyInfo(userId)
        return BaseResponse(data = response)
    }

    // 내정보 수정
    @PutMapping("/info")
    fun saveMyInfo(@RequestBody @Valid memberDtoRequest: MemberDtoRequest): BaseResponse<Unit> {
        //val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        val userId = 1L
        memberDtoRequest.id = userId
        val resultMsg: String = memberService.saveMyInfo(memberDtoRequest)
        return BaseResponse(message = resultMsg)
    }
}