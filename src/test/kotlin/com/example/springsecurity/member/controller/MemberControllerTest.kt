package com.example.springsecurity.member.controller

import com.example.springsecurity.common.status.Gender
import com.example.springsecurity.member.dto.MemberDtoRequest
import com.example.springsecurity.member.repository.MemberRepository
import com.example.springsecurity.member.repository.MemberRoleRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.async.methods.BasicHttpRequests.post
import io.kotest.assertions.print.print
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity.status
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate

//@SpringBootTest
@WebMvcTest
@ActiveProfiles("test")
class MemberControllerTest : BehaviorSpec() {

    init {
        val mockMvc = mockk<MockMvc>()
        val memberRepository = mockk<MemberRepository>()
        val memberController = mockk<MemberController>()
        //val objectMapper = mockk<Jackson2ObjectMapperBuilder>()



        Given("회원가입을 할 때") {
            val memberDtoRequest = MemberDtoRequest(
                id = null,
                _loginId = "test",
                _password = "test",
                _name = "test",
                _email = "tmfrl1570@naver.com",
                _gender = Gender.MAN.toString(),
                _birthDate = LocalDate.now().toString()
            )
            val jsonBody = jacksonObjectMapper().writeValueAsString(memberDtoRequest)


            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/member/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody)
            )
                .andDo(MockMvcResultHandlers.print())


            When("정상적인 데이터로 가입을 하면") {
                val a = memberController.signUp(memberDtoRequest)

                Then("회원가입이 성공적으로 완료된다.") {
                    a.message shouldBe "회원가입이 완료되었습니다."

                    memberRepository.findByLoginId(memberDtoRequest.loginId)?.name shouldBe "test"
                }
            }
        }

        Given("회원가입을 하고") {
            val memberDtoRequest = MemberDtoRequest(
                id = 1L,
                _loginId = "test",
                _password = "test",
                _name = "test",
                _email = "tmfrl1570@naver.com",
                _gender = Gender.MAN.toString(),
                _birthDate = LocalDate.now().toString()
            )
            val a = memberController.signUp(memberDtoRequest)

            val memberDtoRequest1 = MemberDtoRequest(
                id = 1L,
                _loginId = "test",
                _password = "test",
                _name = "test11",
                _email = "tmfrl1570@naver.com",
                _gender = Gender.MAN.toString(),
                _birthDate = LocalDate.now().toString()
            )
            When("회원정보 수정을 하면") {
                memberController.saveMyInfo(memberDtoRequest1)
                Then("제대로 수정된다.") {
                    memberRepository.findByIdOrNull(a.data!!.id)?.loginId shouldBe "test"
                    memberRepository.findByLoginId("test")!!.name shouldNotBe "test11"
                }
            }
        }
    }

    /*beforeEach{
        memberRoleRepository.deleteAll()
        memberRepository.deleteAll()
    }*/

    /*Given("회원가입을 할 때"){
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
            val a = memberController.signUp(memberDtoRequest)

            Then("회원가입이 성공적으로 완료된다."){
                a.message shouldBe "회원가입이 완료되었습니다."

                memberRepository.findByLoginId(memberDtoRequest.loginId)?.name shouldBe "test"
            }
        }
    }

    Given("회원가입을 하고"){
        val memberDtoRequest = MemberDtoRequest(
            id = 1L,
            _loginId = "test",
            _password = "test",
            _name = "test",
            _email = "tmfrl1570@naver.com",
            _gender = Gender.MAN.toString(),
            _birthDate = LocalDate.now().toString()
        )
        val a = memberController.signUp(memberDtoRequest)

        val memberDtoRequest1 = MemberDtoRequest(
            id = 1L,
            _loginId = "test",
            _password = "test",
            _name = "test11",
            _email = "tmfrl1570@naver.com",
            _gender = Gender.MAN.toString(),
            _birthDate = LocalDate.now().toString()
        )
        When("회원정보 수정을 하면"){
            memberController.saveMyInfo(memberDtoRequest1)
            Then("제대로 수정된다."){
                memberRepository.findByIdOrNull(a.data!!.id)?.loginId shouldBe "test"
                memberRepository.findByLoginId("test")!!.name shouldNotBe "test11"
            }
        }
    }*/

}

