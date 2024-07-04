package me.podsialdy.api.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class CookieConfig {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.path}")
    private String cookiePath;

    @Value("${jwt.cookie.httponly}")
    private boolean cookieHttpOnly;
    
    @Value("${jwt.cookie.age}")
    private int cookieAge;

}
