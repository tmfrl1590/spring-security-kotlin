package com.example.springsecurity.member.repository

import com.example.springsecurity.member.entity.Member
import com.example.springsecurity.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {

    fun findByLoginId(loginId: String?): Member?
}

interface MemberRoleRepository: JpaRepository<MemberRole, Long>