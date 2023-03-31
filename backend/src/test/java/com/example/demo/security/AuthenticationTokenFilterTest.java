package com.example.demo.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.config.security.AuthProperties;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AuthenticationTokenFilterTest {

    private static final String LOGIN_ID = "tester12345";
    private static final String PASSWORD = "12345678";
    private static final String NICKNAME = "tester";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "bearer ";


    @MockBean
    private MemberDetailsService memberDetailsService;

    @Autowired
    private AuthProperties authProperties;

    private MockMvc mvc;
    private String accessToken;
    private Member member;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        accessToken = generateToken(30000L);

        member = Member.builder()
            .loginId(LOGIN_ID)
            .password(PASSWORD)
            .nickname(NICKNAME)
            .role(Role.ROLE_USER)
            .build();
    }

    @Test
    void permissionTest() throws Exception {
        mvc.perform(get("/api/members"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void sendAccessToken() throws Exception {
        given(memberDetailsService.loadUserByUsername(anyString())).willReturn(MemberDetails.create(member));
        mvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + accessToken))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    void sendExpiredToken() throws Exception {
        given(memberDetailsService.loadUserByUsername(anyString())).willReturn(MemberDetails.create(member));
        String expiredToken = generateToken(1000L);
        Thread.sleep(2000L);
        mvc.perform(get("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + expiredToken))
            .andExpect(status().isUnauthorized())
            .andDo(print());
        verify(memberDetailsService, never()).loadUserByUsername(anyString());
    }

    private String generateToken(Long time) {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, authProperties.getAuth().getTokenSecret())
            .setExpiration(new Date(System.currentTimeMillis() + time))
            .setSubject(LOGIN_ID)
            .compact();
    }
}
