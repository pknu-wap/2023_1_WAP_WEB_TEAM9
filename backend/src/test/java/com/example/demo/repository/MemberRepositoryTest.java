package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.member.Member;
import java.util.Optional;
import javax.sql.rowset.BaseRowSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
            .loginId("tester1234")
            .password("12345678")
            .nickname("test")
            .build();
        memberRepository.save(member);
    }

    @Test
    void findMemberByLoginId() {
        Optional<Member> memberByLoginId = memberRepository.findMemberByLoginId(member.getLoginId());

        if (memberByLoginId.isEmpty()) {
            throw new AssertionError("멤버를 찾을 수 없습니다.");
        }

        Member foundMember = memberByLoginId.get();
        assertThat(foundMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(foundMember.getPassword()).isEqualTo(member.getPassword());
    }
}
