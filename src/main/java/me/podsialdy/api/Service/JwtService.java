package me.podsialdy.api.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import me.podsialdy.api.Entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.RefreshTokenRepository;
import me.podsialdy.api.Utils.JwtConfig;

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

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;
    private final long EXPIRATION_DURATION;
    private final String CLAIM_ROLES;
    private final String CLAIM_SCOPE;
    private final String CLAIM_SESSION;
    private final String AUTH_SCOPE;
    private final String ISSUER;
    private final String SECRET_KEY;
    private final String PRE_AUTH_SCOPE;

    @Autowired
    public JwtService(RefreshTokenRepository refreshTokenRepository, JwtConfig jwtConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtConfig = jwtConfig;
        this.EXPIRATION_DURATION = jwtConfig.getExpirationDuration();
        this.CLAIM_ROLES = jwtConfig.getClaimRoles();
        this.CLAIM_SCOPE = jwtConfig.getClaimScope();
        this.CLAIM_SESSION = jwtConfig.getClaimSession();
        this.AUTH_SCOPE = jwtConfig.getAuthScope();
        this.ISSUER = jwtConfig.getIssuer();
        this.SECRET_KEY = jwtConfig.getSecretKey();
        this.PRE_AUTH_SCOPE = jwtConfig.getPreAuthScope();
    }

    /**
     * Generates a JWT token for the provided customer.
     * 
     * @param customer The customer to whom the token will be generated
     */
    public String generateToken(Customer customer) {

        log.info("Generate token for user {}", customer.getId());

        try {

            String jwt = createToken(customer.getUsername(), getUserRoles(customer.getRoles()),
                    PRE_AUTH_SCOPE, generateUUID(), EXPIRATION_DURATION);

            log.info("token generate for user {}", customer.getId());

            log.info("JWt generate {}", jwt);

            return jwt;
        } catch (Exception e) {
            log.error("Error while generating token", e);
        }

        return null;
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

        try {

            String jwt = createToken(getSubject(token), getRoles(token),
                    AUTH_SCOPE, getSession(token), EXPIRATION_DURATION);

            log.info("Token is granted {}", jwt);

            return jwt;
        } catch (Exception e) {
            log.error("Error while granting aceess token", e);
        }

        return null;
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
        return decodedJWT.getClaim(CLAIM_SCOPE).asString();
    }

    /**
     * 
     * get the claim that contains user's roles
     * 
     * @param token
     * @return list of user's roles
     * @throws JWTVerificationException
     */
    public String[] getRoles(String token) throws JWTVerificationException {
        if (!verifyToken(token))
            throw new JWTVerificationException("Invalid token");
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim(CLAIM_ROLES).asArray(String.class);
    }

    /**
     * Retrieves the session ID from the provided JWT token.
     * 
     * @param token The JWT token from which to extract the session ID
     * @return The session ID extracted from the token
     */
    public UUID getSession(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim(CLAIM_SESSION).as(UUID.class);
    }

    /**
     * Verifies the validity of the provided JWT token.
     * 
     * @param token The JWT token to be verified
     * @return true if the token is valid, false otherwise
     */
    public boolean verifyToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).withIssuer(ISSUER).build();
        try {
            log.info("Token validation in progress...");
            jwtVerifier.verify(token);
            log.info("Token is valid");
            return true;
        } catch (Exception e) {
            log.error("Error while verifying the token", e);
        }

        return false;
    }

    /**
     * Retrieves the roles from the provided set of Role objects.
     * 
     * @param roles The set of Role objects from which to extract roles
     * @return An array of roles extracted from the set of Role objects
     */
    public String[] getUserRoles(Set<Role> roles) {
        return roles.stream().map(Role::getRole).toArray(String[]::new);
    }

    /**
     * Generates a unique UUID string that does not exist in the
     * refreshTokenRepository.
     * Attempts up to a maximum of 10 times to generate a unique UUID.
     *
     * @return A unique UUID string
     * @throws RuntimeException if unable to generate a unique UUID after 10
     *                          attempts
     */
    public UUID generateUUID() {
        UUID uuid;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            if (attempts >= maxAttempts) {
                log.error("Failed to generate a unique UUID after {} attempts", maxAttempts);
                throw new RuntimeException("Unable to generate unique UUID");
            }
            uuid = UUID.randomUUID();
            attempts++;
        } while (refreshTokenRepository.findBySessionId(uuid).isPresent());

        log.info("Generated unique UUID after {} attempts", attempts);
        return uuid;
    }

    /**
     * Creates a new refresh token based on the provided token.
     * 
     * @param token The JWT token from which to create the refresh token
     * @return The newly generated refresh token, or null if an error occurs
     */
    public String refreshToken(String token) {

        log.info("Attempt to create a refresh token");

        try {
            String jwt = createToken(getSubject(token), getRoles(token),
                    getScope(token), getSession(token), EXPIRATION_DURATION);

            log.info("Refresh token create {}", jwt);

            return jwt;

        } catch (Exception e) {
            log.error("Error while creating a refresh token", e);
        }

        return null;

    }

    /**
     * Creates a user token based on the provided token.
     * 
     * @param token The JWT token from which to create the user token
     * @return The newly generated user token
     */
    public String getUserToken(String token) {

        log.info("Attempt to create user token");

        try {
            String authState = getScope(token);

            String userAuthState = authState.equals(AUTH_SCOPE) ? "auth"
                    : authState.equals(PRE_AUTH_SCOPE) ? "pre_auth" : "";

            String jwt = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(getSubject(token))
                    .withArrayClaim(CLAIM_ROLES, getRoles(token))
                    .withExpiresAt(Instant.now().plusSeconds(EXPIRATION_DURATION))
                    .withClaim("auth_state", userAuthState)
                    .sign(Algorithm.HMAC256(SECRET_KEY));

            log.info("User info token create {}", jwt);

            return jwt;
        } catch (Exception e) {
            log.error("Error while creating user info token", e);
        }
        return null;

    }

    /**
     * Creates a JWT token based on the provided subject, roles, scope, session ID,
     * and duration.
     * 
     * @param subject         The subject of the token
     * @param roles           An array of roles associated with the token
     * @param scope           The scope of the token
     * @param sessionId       The session ID linked to the token
     * @param durationMinutes The duration of the token validity in minutes
     * @return The generated JWT token as a string
     */
    public String createToken(String subject, String[] roles, String scope, UUID sessionId, long durationMinutes) {

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(subject)
                .withArrayClaim(CLAIM_ROLES, roles)
                .withExpiresAt(Instant.now().plus(durationMinutes, ChronoUnit.MINUTES))
                .withClaim(CLAIM_SCOPE, scope)
                .withClaim(CLAIM_SESSION, sessionId.toString())
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

}
