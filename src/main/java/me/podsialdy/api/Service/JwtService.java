package me.podsialdy.api.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.Role;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(Customer customer) {

        log.info("Generate token for user {}", customer.getId());

        String jwt = JWT
                .create()
                .withIssuer("portfolio-api")
                .withSubject(customer.getUsername())
                .withArrayClaim("roles", getUserRoles(customer.getRoles()))
                .withExpiresAt(Instant.now().plus(15L, ChronoUnit.MINUTES))
                .withClaim("scope", "pre_auth")
                .sign(Algorithm.HMAC256(secretKey));

        log.info("token generate for user {}", customer.getId());

        log.info("JWt generate {}", jwt);

        return jwt;
    }

    public String grantAccessToken(String token) throws JWTVerificationException {

        if (!verifyToken(token))
            throw new JWTVerificationException("Invalid token");

        DecodedJWT decodedJWT = JWT.decode(token);
        String jwt = JWT.create()
        .withIssuer(decodedJWT.getIssuer())
        .withSubject(decodedJWT.getSubject())
        .withArrayClaim("roles", decodedJWT.getClaim("roles").asArray(String.class))
        .withExpiresAt(Instant.now().plusSeconds(60*10))
        .withClaim("scope", "auth")
        .sign(Algorithm.HMAC256(secretKey));

        return jwt;
    }

    public String getSubject(String token) throws JWTVerificationException {
        if(!verifyToken(token))
            throw new JWTVerificationException("Invalid token");
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    public String getScope(String token) throws JWTVerificationException {
        if(!verifyToken(token))
            throw new JWTVerificationException("Invalid token");
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("scope").asString();
    }

    public boolean verifyToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey)).withIssuer("portfolio-api").build();
        try {
            log.info("Token validation in progress...");
            jwtVerifier.verify(token);
            log.info("Token is valid");
            return true;
        } catch (JWTVerificationException e) {
            log.warn("Invalid token : {}", e);
            return false;
        } catch (Exception e) {
            log.error("An error occured during validation token: {}", e);
            return false;
        }
    }

    public String[] getUserRoles(Set<Role> roles) {
        List<String> rolesList = roles.stream().map(role -> role.getRole()).collect(Collectors.toList());
        String[] rolesArray = rolesList.toArray(new String[0]);
        return rolesArray;
    }

}
