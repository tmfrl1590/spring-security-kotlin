package com.example.springsecurity.member.service

import com.example.springsecurity.common.status.Gender
import com.example.springsecurity.member.dto.MemberDtoRequest
import com.example.springsecurity.member.repository.MemberRepository
import com.example.springsecurity.member.repository.MemberRoleRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val memberRoleRepository: MemberRoleRepository,
) : BehaviorSpec({



    beforeEach{
        memberRoleRepository.deleteAll()
        memberRepository.deleteAll()
    }


    Given("회원가입을 할 때"){
            val memberDtoRequest = MemberDtoRequest(
                id = null,
                _loginId = "test",
                _password = "test",
                _name = "test",
                _email = "tmfrl1570@naver.com",
                _gender = Gender.MAN.toString(),
                _birthDate = LocalDate.now().toString()
            )


            When("정상적인 데이터로 가입을 하면"){
                memberService.signUp(memberDtoRequest)

                val getMember = memberRepository.findByLoginId("test")
                Then("회원가입이 성공적으로 완료된다."){

                    getMember!!.loginId shouldBe "test"
                    getMember!!.loginId shouldNotBe "test1"
                }
            }

    }

    // 내정보 조회 테스트
    Given("내정보를 조회할 때"){
        val memberDtoRequest = MemberDtoRequest(
            id = null,
            _loginId = "test2",
            _password = "test",
            _name = "test",
            _email = "tmfrl1570@naver.com",
            _gender = Gender.MAN.toString(),
            _birthDate = LocalDate.now().toString()
        )

        val savedMember = memberService.signUp(memberDtoRequest)
        println("$savedMember")

        When("정상적인 데이터로 회원가입을 하면"){
            val getMember = memberService.searchMyInfo(savedMember.id!!)
            println("$getMember")

            Then("제대로 조회가 된다"){
                getMember.loginId shouldBe "test2"
                getMember.loginId shouldNotBe "test1"
                getMember.name shouldBe "test"
            }
        }
    }
})