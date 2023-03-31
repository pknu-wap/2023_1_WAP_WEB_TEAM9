package com.example.demo.controller;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.dto.MemberResponse;
import com.example.demo.security.LoginMember;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/api/members")
public class MemberController {

    @GetMapping
    public MemberResponse getAuthenticationMember(@LoginMember Member member) {
        return MemberResponse.of(member);
    }
}
