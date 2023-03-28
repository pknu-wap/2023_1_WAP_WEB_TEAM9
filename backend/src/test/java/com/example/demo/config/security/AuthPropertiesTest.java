package com.example.demo.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthPropertiesTest {

    @Autowired
    private AuthProperties authProperties;

    @Test
    void getTokenSecret() {
        String tokenSecret = authProperties.getAuth().getTokenSecret();
        System.out.println("tokenSecret = " + tokenSecret);
        assertThat(tokenSecret).isNotNull();
    }

    @Test
    void getExpireTime() {
        Long expireTime = authProperties.getAuth().getExpireTime();
        System.out.println("expireTime = " + expireTime);
        assertThat(expireTime).isNotNull();
    }
}
