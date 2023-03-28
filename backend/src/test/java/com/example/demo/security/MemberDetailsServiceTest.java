package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.member.Member;
import com.example.demo.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class MemberDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberDetailsService memberDetailsService;
    private Member member;

    @BeforeEach
    void init() {
        memberDetailsService = new MemberDetailsService(memberRepository);
        member = Member.builder()
            .loginId("tester12345")
            .password("12345678")
            .nickname("tester")
            .build();
    }

    @Test
    void loadUserByUsername() {
        when(memberRepository.findMemberByLoginId(anyString())).thenReturn(Optional.of(member));
        UserDetails userDetails = memberDetailsService.loadUserByUsername(member.getLoginId());

        assertThat(userDetails.getPassword()).isEqualTo(member.getPassword());
        verify(memberRepository).findMemberByLoginId(anyString());
    }

    @Test
    void loadNotUser() {
        when(memberRepository.findMemberByLoginId(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberDetailsService.loadUserByUsername("notUser"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
