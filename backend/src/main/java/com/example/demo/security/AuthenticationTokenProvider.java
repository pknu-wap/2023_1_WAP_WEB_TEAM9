package com.example.demo.security;

import com.example.demo.config.security.AuthProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationTokenProvider {

    private final AuthProperties authProperties;

    public String generateAccessToken(Authentication authentication) {
        String loginId = getLoginId(authentication);
        return Jwts.builder()
            .setSubject(loginId)
            .setExpiration(new Date(System.currentTimeMillis() + authProperties.getAuth().getExpireTime()))
            .signWith(SignatureAlgorithm.HS512, authProperties.getAuth().getTokenSecret())
            .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser().setSigningKey(authProperties.getAuth().getTokenSecret())
                .parseClaimsJws(accessToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getAttribute(String accessToken) {
        return Jwts.parser().setSigningKey(authProperties.getAuth().getTokenSecret())
            .parseClaimsJws(accessToken)
            .getBody()
            .getSubject();
    }

    private String getLoginId(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }
}
