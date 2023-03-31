package com.example.demo.common;

import com.example.demo.domain.member.Member;
import com.example.demo.security.MemberDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMemberSecurityContextFactory implements WithSecurityContextFactory<WithCustomMember> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMember annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Member member = getMember(annotation);
        MemberDetails memberDetails = MemberDetails.create(member);

        UsernamePasswordAuthenticationToken token = generateAuthenticationToken(memberDetails);
        context.setAuthentication(token);

        return context;
    }

    private UsernamePasswordAuthenticationToken generateAuthenticationToken(MemberDetails memberDetails) {
        return new UsernamePasswordAuthenticationToken(memberDetails,
            memberDetails.getPassword(),
            memberDetails.getAuthorities());
    }

    private Member getMember(WithCustomMember annotation) {
        return Member.builder()
            .id(annotation.id())
            .loginId(annotation.loginId())
            .password(annotation.password())
            .nickname(annotation.nickname())
            .role(annotation.role())
            .build();
    }
}
