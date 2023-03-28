package com.example.demo.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.config.WebSecurityConfig;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.domain.member.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(WebSecurityConfig.class)
@WebMvcTest
class LoginFilterTest {

    private static final String LOGIN_ID = "tester12345";
    private static final String PASSWORD = "12345678";
    private static final String NICKNAME = "tester";

    @MockBean
    private MemberDetailsService memberDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mvc;
    private LoginRequest request;
    private ObjectMapper objectMapper;
    private Member member;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        member = Member.builder()
            .loginId(LOGIN_ID)
            .nickname(NICKNAME)
            .password(passwordEncoder.encode(PASSWORD))
            .role(Role.USER)
            .build();
        request = new LoginRequest(LOGIN_ID, PASSWORD);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("올바른 아이디 비밀번호를 입력하면 로그인이 성공한다.")
    @Test
    void login() throws Exception {
        given(memberDetailsService.loadUserByUsername(anyString())).willReturn(MemberDetails.create(member));
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value(LOGIN_ID))
            .andDo(print());
    }

    @DisplayName("아이디 비밀번호가 맞지 않으면 로그인이 실패한다.")
    @MethodSource("failLoginRequests")
    @ParameterizedTest
    void inputFailLoginRequest(LoginRequest failRequest) throws Exception {
        given(memberDetailsService.loadUserByUsername(anyString())).willReturn(MemberDetails.create(member));
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
