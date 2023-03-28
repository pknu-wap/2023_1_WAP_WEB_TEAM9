package com.example.demo.security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.config.WebSecurityConfig;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(WebSecurityConfig.class)
@WebMvcTest
class LoginFilterTest {

    private MockMvc mvc;
    private LoginRequest request;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        request = new LoginRequest("test", "1234");

        objectMapper = new ObjectMapper();
    }

    @DisplayName("올바른 아이디 비밀번호를 입력하면 로그인이 성공한다.")
    @Test
    void login() throws Exception {
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value("test"))
            .andDo(print());
    }

    @DisplayName("아이디 비밀번호가 맞지 않으면 로그인이 실패한다.")
    @MethodSource("failLoginRequests")
    @ParameterizedTest
    void inputFailLoginRequest(LoginRequest failRequest) throws Exception {
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(failRequest)))
            .andExpect(status().isUnauthorized())
            .andDo(print());
    }

    @DisplayName("아이디와 비밀번호 중 둘중 하나가 비면 로그인이 실패한다.")
    @MethodSource("blankLoginRequests")
    @ParameterizedTest
    void inputBlankLoginRequest(LoginRequest blankRequest) throws Exception {
        mvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(blankRequest)))
            .andExpect(status().isUnauthorized())
            .andDo(print());
    }

    private static Stream<Arguments> failLoginRequests() {
        return Stream.of(
            Arguments.arguments(new LoginRequest("notUser", "1234")),
            Arguments.arguments(new LoginRequest("test", "12345"))
        );
    }

    private static Stream<Arguments> blankLoginRequests() {
        return Stream.of(
            Arguments.arguments(new LoginRequest("", "1234")),
            Arguments.arguments(new LoginRequest("test", "")),
            Arguments.arguments(new LoginRequest("", ""))
        );
    }

    @TestConfiguration
    static class testSecurityConfig {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean
        public UserDetailsService userDetailsService() {
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            UserDetails user = User.withUsername("test")
            .password(passwordEncoder.encode("1234"))
            .authorities("USER")
            .build();
            manager.createUser(user);
            return manager;
        }
    }
}
