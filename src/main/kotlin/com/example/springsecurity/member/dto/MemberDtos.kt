package com.example.springsecurity.member.dto

import com.example.springsecurity.common.annotation.ValidEnum
import com.example.springsecurity.common.status.Gender
import com.example.springsecurity.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.cglib.core.Local
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// validation 을 추가하려면 널을 허용하는 타입으로 바꾸고, 데이터를 담아야 하는데
// 커스텀 게터를 이용한다.
// 생일과 성별은 String으로 받고 체크를 한 후에 각각의 타입으로 내보낸다.
data class MemberDtoRequest(
    var id: Long?,

    @field:NotBlank
    @JsonProperty("loginId")   // _loginId와 연결
    private val _loginId: String?,

    @field:NotBlank
    @field:Pattern(
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요"
    )
    @JsonProperty("password")
    private val _password: String?,

    @field:NotBlank
    @JsonProperty("name")
    private val _name: String?,

    @field:NotBlank
    @field:Pattern(
        regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
        message = "날짜형식(YYYY-MM-DD)을 확인해주세요"
    )
    @JsonProperty("birthDate")
    private val _birthDate: String?,

    @field:NotBlank
    @field:ValidEnum(enumClass = Gender::class, message = "MAN 이나 WOMAN 중 하나를 선택해주세요")
    @JsonProperty("gender")
    private val _gender: String?,

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    private val _email: String,
){
    val loginId: String
        get() = _loginId!!
    val password: String
        get() = _password!!
    val name: String
        get() = _name!!
    val birthDate: LocalDate
        get() = _birthDate!!.toLocalDate()
    val gender: Gender
        get() = Gender.valueOf(_gender!!)
    val email: String
        get() = _email!!

    // String.toLocalDate() 을 호출하면 LocalDate 타입으로 반환 / String 이 this 를 의미함
    private fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    fun toEntity(): Member = Member(id, loginId, password, name, birthDate, gender, email)
}


data class LoginDto(
    @field:NotBlank(
        message = "아이디가 비어있어요"
    )
    @JsonProperty("loginId")
    private val _loginId: String?,

    @field:NotBlank(
        message = "패스워드는 공백이 아니야"
    )
    @JsonProperty("password")
    private val _password: String?

){
    val loginId: String
        get() = _loginId!!
    val password: String
        get() = _password!!
}

data class MemberDtoResponse(
    val id: Long,
    val loginId: String,
    val name: String,
    val birthDate: String,
    val gender: String,
    val email: String,
)