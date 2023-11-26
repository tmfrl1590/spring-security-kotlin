package com.example.springsecurity.common.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [ValidEnumValidator::class])
annotation class ValidEnum (
    val message: String = "Invalid enum value",  // 유효성 검사 통과하지 못하면 message 가 나옴
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val enumClass: KClass<out Enum<*>>      // Gender enum 클래스가 들어올자리
    )

class ValidEnumValidator : ConstraintValidator<ValidEnum, Any> {
    private lateinit var enumValues: Array<out Enum<*>>

    override fun initialize(annotation: ValidEnum) {
        enumValues = annotation.enumClass.java.enumConstants
    }
    override fun isValid(value: Any?, context: ConstraintValidatorContext?): Boolean {  // value 사용자로부터 입력받은 값이 들어옴
        if(value == null){
            return true
        }
        return enumValues.any { it.name == value.toString()} // any 뒤에 있는 조건이 하나라도 만족이면 true
    }

}
