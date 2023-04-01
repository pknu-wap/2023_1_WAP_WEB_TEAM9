package com.example.demo.security;

import com.example.demo.domain.member.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String AUTHENTICATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final AuthenticationTokenProvider provider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        String accessToken = provider.generateAccessToken(authentication);
        response.addHeader(AUTHENTICATION, BEARER + accessToken);
    }
}
