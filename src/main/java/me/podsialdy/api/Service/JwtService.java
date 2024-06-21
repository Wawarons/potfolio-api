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

/**
 * Service class for handling JWT token generation, verification, and access
 * granting.
 * This class provides methods to generate a token for a given customer, grant
 * access token based on a provided token,
 * retrieve subject and scope from a token, and verify the validity of a token.
 * It also includes a method to extract user roles from a set of roles.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.scope.pre_auth}")
    private String preAuthScope;

    @Value("${jwt.scope.auth}")
    private String authScope;

    /**
     * Generates a JWT token for the provided customer.
     * 
     * @param customer The customer to whom the token will be generated
     */
    public String generateToken(Customer customer) {

        log.info("Generate token for user {}", customer.getId());

        String jwt = JWT
                .create()
                .withIssuer(issuer)
                .withSubject(customer.getUsername())
                .withArrayClaim("roles", getUserRoles(customer.getRoles()))
                .withExpiresAt(Instant.now().plus(15L, ChronoUnit.MINUTES))
                .withClaim("scope", preAuthScope)
                .sign(Algorithm.HMAC256(secretKey));

        log.info("token generate for user {}", customer.getId());

        log.info("JWt generate {}", jwt);

        return jwt;
    }

    /**
     * Grants access token based on the provided token.
     * 
     * @param token The JWT token to be used for granting access
     * @return The newly generated access token
     * @throws JWTVerificationException If the provided token is invalid
     */
    public String grantAccessToken(String token) throws JWTVerificationException {

        log.info("Attempt to grant access token");

        if (!verifyToken(token))
            throw new JWTVerificationException("Invalid token");

        DecodedJWT decodedJWT = JWT.decode(token);
        String jwt = JWT.create()
                .withIssuer(decodedJWT.getIssuer())
                .withSubject(decodedJWT.getSubject())
                .withArrayClaim("roles", decodedJWT.getClaim("roles").asArray(String.class))
                .withExpiresAt(Instant.now().plusSeconds(60 * 10))
                .withClaim("scope", authScope)
                .sign(Algorithm.HMAC256(secretKey));

        log.info("Token is granted {}", jwt);

        return jwt;
    }

    /**
     * get the token's subject
     * 
     * @param token The JWT token to be verified
     * @return The subject of the token if it is valid, or an empty string if not
     */
    public String getSubject(String token) throws JWTVerificationException {
        if (!verifyToken(token))
            throw new JWTVerificationException("Invalid token");
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    /**
     * get the token's scope
     * 
     * @param token The JWT token to be verified
     * @return The scope of the token if it is valid, or an empty string if not
     */
    public String getScope(String token) throws JWTVerificationException {
        if (!verifyToken(token))
            throw new JWTVerificationException("Invalid token");
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("scope").asString();
    }

    /**
     * Verifies the validity of the provided JWT token.
     * 
     * @param token The JWT token to be verified
     * @return true if the token is valid, false otherwise
     */
    public boolean verifyToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey)).withIssuer(issuer).build();
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

    /**
     * Retrieves the roles from the provided set of Role objects.
     * 
     * @param roles The set of Role objects from which to extract roles
     * @return An array of roles extracted from the set of Role objects
     */
    public String[] getUserRoles(Set<Role> roles) {
        List<String> rolesList = roles.stream().map(role -> role.getRole()).collect(Collectors.toList());
        String[] rolesArray = rolesList.toArray(new String[0]);
        return rolesArray;
    }

}
