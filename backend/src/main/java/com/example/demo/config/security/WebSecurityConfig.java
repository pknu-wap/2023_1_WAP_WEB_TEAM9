package com.example.demo.config.security;

import com.example.demo.security.AuthenticationTokenFilter;
import com.example.demo.security.AuthenticationTokenProvider;
import com.example.demo.security.LoginFilter;
import com.example.demo.security.LoginSuccessHandler;
import com.example.demo.security.MemberAuthenticationEntryPoint;
import com.example.demo.security.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationTokenProvider tokenProvider;
    private final MemberDetailsService memberDetailsService;
    private final MemberAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public AuthenticationTokenFilter jwtFilter() {
        return new AuthenticationTokenFilter(tokenProvider, memberDetailsService);
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setFilterProcessesUrl("/api/auth/login");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        return loginFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET ,"/api/board/**")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/api/board/**")
            .hasAnyRole("USER", "ADMIN")
            .antMatchers(HttpMethod.PATCH, "/api/board/**")
            .hasAnyRole("USER", "ADMIN")
            .antMatchers(HttpMethod.DELETE, "/api/board/**")
            .hasAnyRole("USER", "ADMIN")
            .anyRequest().permitAll()
            .and()
            .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
