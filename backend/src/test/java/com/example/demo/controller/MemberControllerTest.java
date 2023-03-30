package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.common.WithCustomMember;
import com.example.demo.config.WebConfig;
import com.example.demo.config.security.AuthProperties;
import com.example.demo.config.security.WebSecurityConfig;
import com.example.demo.security.AuthenticationTokenProvider;
import com.example.demo.security.MemberDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    private MockMvc mvc;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }

    @Test
    @WithCustomMember
    void test() throws Exception {
        mvc.perform(get("/api/members")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }
}

