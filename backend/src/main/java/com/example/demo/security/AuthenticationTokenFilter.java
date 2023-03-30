package com.example.demo.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String JWT_HEADER = "Authorization";

    private final AuthenticationTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            authenticate(request);
        } catch (Exception e) {
            log.error("error={}", e.getClass());
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.hasText(token) && tokenProvider.validateAccessToken(token)) {
            String username = tokenProvider.getAttribute(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()));
        }
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(JWT_HEADER);
        return header.substring(7);
    }
}
