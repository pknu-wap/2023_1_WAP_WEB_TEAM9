package com.example.demo.security;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.config.security.AuthProperties;
import com.example.demo.config.security.WebSecurityConfig;
import com.example.demo.controller.MemberController;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.domain.member.dto.LoginRequest;
import com.example.demo.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import({WebSecurityConfig.class, AuthProperties.class})
@ComponentScan(basePackages = "com.example.demo.security")
@WebMvcTest(MemberController.class)
class LoginFilterTest {

    @MockBean
    private MemberRepository memberRepository;

    private MockMvc mvc;
    private LoginRequest request;
    private ObjectMapper objectMapper;
    private Member member;

    @BeforeEach
    void init(WebApplicationContext context) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        member = Member.builder()
            .loginId(LOGIN_ID)
            .nickname(NICKNAME)
            .password(passwordEncoder.encode(PASSWORD))
            .role(Role.ROLE_USER)
            .build();
        request = new LoginRequest(LOGIN_ID, PASSWORD);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("올바른 아이디 비밀번호를 입력하면 로그인이 성공한다.")
    @Test
    void login() throws Exception {
        given(memberRepository.findMemberByLoginId(anyString())).willReturn(Optional.of(member));
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"))
            .andDo(print());
    }

    @DisplayName("아이디 비밀번호가 맞지 않으면 로그인이 실패한다.")
    @MethodSource("failLoginRequests")
    @ParameterizedTest
    void inputFailLoginRequest(LoginRequest failRequest) throws Exception {
        given(memberRepository.findMemberByLoginId(anyString())).willReturn(Optional.of(member));
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(failRequest)))
            .andExpect(status().isUnauthorized())
            .andDo(print());
    }

    private static Stream<Arguments> failLoginRequests() {
        return Stream.of(
            Arguments.arguments(new LoginRequest(LOGIN_ID, "12345"))
        );
    }
}
