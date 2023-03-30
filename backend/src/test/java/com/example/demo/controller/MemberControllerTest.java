package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.WithCustomMember;
import com.example.demo.config.WebConfig;
import com.example.demo.config.security.AuthProperties;
import com.example.demo.config.security.WebSecurityConfig;
import com.example.demo.security.AuthenticationTokenProvider;
import com.example.demo.security.MemberDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(WebSecurityConfig.class)
@ComponentScan(basePackages = "com.example.demo.security")
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    private AuthProperties authProperties;

    @MockBean
    private AuthenticationTokenProvider tokenProvider;

    @MockBean
    private MemberDetailsService service;

    @Test
    @WithCustomMember
    void test() {

    }
}

