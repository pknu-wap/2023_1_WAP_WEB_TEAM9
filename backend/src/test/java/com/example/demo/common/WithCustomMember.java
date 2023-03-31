package com.example.demo.common;

import com.example.demo.domain.member.Role;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMemberSecurityContextFactory.class)
public @interface WithCustomMember {

    long id() default 1L;
    String loginId() default "member123";
    String password() default "123455678";
    String nickname() default "member";
    Role role() default Role.ROLE_USER;
}
