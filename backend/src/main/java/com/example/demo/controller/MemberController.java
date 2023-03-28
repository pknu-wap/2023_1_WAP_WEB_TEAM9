package com.example.demo.controller;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.dto.MemberResponse;
import com.example.demo.security.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @GetMapping
    public MemberResponse getAuthenticationMember(Authentication authentication) {
        return MemberResponse.of(getMember(authentication));
    }

    private Member getMember(Authentication authentication) {
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();
        return principal.getMember();
    }
}
