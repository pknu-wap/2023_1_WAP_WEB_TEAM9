package com.example.demo.domain.member;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
                .loginId(LOGIN_ID)
                .nickname(NICKNAME)
                .password(PASSWORD)
            .build();
    }

    @Test
    void saveMember() {
        Member savedMember = memberRepository.save(member);
        System.out.println("savedMember.getId() = " + savedMember.getCreateAt());
        assertThat(savedMember.getCreateAt()).isNotNull();
        System.out.println("savedMember.getCreateAt() = " + savedMember.getCreateAt());
    }
}
