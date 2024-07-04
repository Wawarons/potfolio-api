package me.podsialdy.api.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.RefreshToken;
import me.podsialdy.api.Repository.RefreshTokenRepository;
import me.podsialdy.api.Utils.JwtConfig;

/**
 * The RefreshTokenService class provides methods to handle refresh tokens for
 * customers.
 * 
 */
@Service
@Slf4j
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;
    private JwtConfig jwtConfig;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService,
            JwtConfig jwtConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtConfig = jwtConfig;
    }

    /**
     * Initializes a refresh token for the given customer and session ID.
     * 
     * This method first checks if a refresh token exists for the provided session
     * ID and if it is expired.
     * If no valid refresh token is found, a new refresh token is created for the
     * customer using the {@link JwtService}.
     * The new refresh token is then saved in the {@link RefreshTokenRepository}.
     * 
     * @param customer  the customer for whom the refresh token is being initialized
     * @param sessionId the session ID associated with the refresh token
     */
    public void initRefreshToken(Customer customer, UUID sessionId) {

        log.info("Init refresh token for {}", customer.getUsername());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findBySessionId(sessionId);

        if (refreshToken.isEmpty() || refreshToken.get().getExpiration().isBefore(Instant.now())) {

            log.info("Create refresh token for customer {}", customer.getUsername());

            String token = jwtService.generateToken(customer);
            DecodedJWT decodedJWT = JWT.decode(token);

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .customer(customer)
                    .sessionId(decodedJWT.getClaim(jwtConfig.getClaimSession()).as(UUID.class))
                    .token(token)
                    .build();

            refreshTokenRepository.save(newRefreshToken);
        }

    }

}
