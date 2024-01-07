package com.example.springsecurity.member.service

import com.example.springsecurity.common.authority.JwtTokenProvider
import com.example.springsecurity.common.authority.TokenInfo
import com.example.springsecurity.common.exception.InvalidInputException
import com.example.springsecurity.common.status.ROLE
import com.example.springsecurity.member.dto.LoginDto
import com.example.springsecurity.member.dto.MemberDtoRequest
import com.example.springsecurity.member.dto.MemberDtoResponse
import com.example.springsecurity.member.entity.Member
import com.example.springsecurity.member.entity.MemberRole
import com.example.springsecurity.member.repository.MemberRepository
import com.example.springsecurity.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    /*
    * 회원가입
    * */
    fun signUp(memberDtoRequest: MemberDtoRequest): Member{

        // Id 중복검사
        var member: Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if(member != null){
            throw InvalidInputException("loginId", "이미 등록된 ID 입니다.")
        }

        member = memberDtoRequest.toEntity()
        val savedMember = memberRepository.save(member)

        val memberRole: MemberRole = MemberRole(null, ROLE.MEMBER, member)
        memberRoleRepository.save(memberRole)

        //return "회원가입이 완료되었습니다."
        return savedMember
    }


    // 로그인 -> 토큰 발행
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password) // 로그인한 정보를 가지고 UsernamePasswordAuthenticationToken 을 발행함
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken) // 매니터빌더에 이 토큰을 전달,  authenticate 가 실행이 될 때 커스텀 유저 디테일즈 서비스에 load user by username이 호출되면서 디비에 있는 멤버 정보와 비교함

        return jwtTokenProvider.createToken(authentication) // authentication가 아무문제 없으면 이 정보를 가지고 토큰을 발행해서 사용자에게 돌려줌
    }

    // 내정보 조회
    fun searchMyInfo(id: Long): MemberDtoResponse {
        val member: Member = memberRepository.findByIdOrNull(id) ?: throw InvalidInputException("id", "회원번호${id}가 존재하지 않는 유저입니다.")
        return member.toDto()
    }

    // 내 정보 수정
    fun saveMyInfo(memberDtoRequest: MemberDtoRequest): String {
        val member: Member = memberDtoRequest.toEntity()
        memberRepository.save(member)
        return "수정되었습니다."
    }
}