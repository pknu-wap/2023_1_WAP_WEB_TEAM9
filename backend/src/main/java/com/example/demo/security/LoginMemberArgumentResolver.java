package com.example.demo.security;


import com.example.demo.domain.member.Member;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Member.class)
            && parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return getMember(authentication);
        }
        return null;
    }

    private Member getMember(Authentication authentication) {
        if (isAnonymousUser(authentication)) {
            return null;
        }
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();
        return principal.getMember();
    }

    private boolean isAnonymousUser(Authentication authentication) {
        return authentication.getPrincipal().getClass() == String.class;
    }
}
