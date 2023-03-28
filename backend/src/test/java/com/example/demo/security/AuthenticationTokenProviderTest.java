package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.config.security.AuthProperties;
import com.example.demo.config.security.AuthProperties.Auth;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationTokenProviderTest {

    private AuthProperties authProperties;
    private AuthenticationTokenProvider authenticationTokenProvider;
    private Member member;
    private String accessToken;
    private String secretKey;

    @BeforeEach
    void init() {
        secretKey = UUID.randomUUID().toString();
        Auth auth = new Auth();
        auth.setTokenSecret(secretKey);
        auth.setExpireTime(3000L);
        authProperties = new AuthProperties();
        authProperties.setAuth(auth);

        authenticationTokenProvider = new AuthenticationTokenProvider(authProperties);

        member = Member.builder()
            .loginId("tester12345")
            .nickname("tester")
            .password("12345678")
            .role(Role.USER)
            .build();

        accessToken = Jwts.builder()
            .setExpiration(new Date(System.currentTimeMillis() + 1000L))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setSubject("tester12345")
            .compact();
    }

    @Test
    void generateAccessToken() {
        MemberDetails details = new MemberDetails(member);
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());

        String accessToken = authenticationTokenProvider.generateAccessToken(authentication);

        System.out.println("accessToken = " + accessToken);
        assertThat(accessToken).isNotBlank();
    }

    @Test
    void validateAccessToken() {
        boolean result = authenticationTokenProvider.validateAccessToken(accessToken);
        assertThat(result).isTrue();
    }

    @Test
    void inputWrongToken() {
        String wrongToken = Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, "key".getBytes())
            .setSubject("tester12345")
            .compact();
        boolean result = authenticationTokenProvider.validateAccessToken(wrongToken);
        assertThat(result).isFalse();
    }

    @Test
    void inputNotToken() {
        String notToken = "hello";
        boolean result = authenticationTokenProvider.validateAccessToken(notToken);
        assertThat(result).isFalse();
    }

    @Test
    void inputBlank() {
        String blank = "";
        boolean result = authenticationTokenProvider.validateAccessToken(blank);
        assertThat(result).isFalse();
    }

    @Test
    void inputExpiredToken() throws InterruptedException {
        Thread.sleep(2000L);
        boolean result = authenticationTokenProvider.validateAccessToken(accessToken);
        assertThat(result).isFalse();
    }

    @Test
    void getAttribute() {
        String loginId = authenticationTokenProvider.getAttribute(accessToken);
        assertThat(loginId).isEqualTo(member.getLoginId());
    }
}
