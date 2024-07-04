package me.podsialdy.api.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.scope.pre_auth}")
    private String preAuthScope;

    @Value("${jwt.scope.auth}")
    private String authScope;

    @Value("${jwt.claim.session}")
    private String claimSession;

    @Value("${jwt.claim.scope}")
    private String claimScope;

    @Value("${jwt.claim.roles}")
    private String claimRoles;

    @Value("${jwt.expiration_duration}")
    private long expirationDuration;

}
