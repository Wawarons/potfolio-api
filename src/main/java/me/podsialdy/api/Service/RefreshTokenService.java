package me.podsialdy.api.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.RefreshToken;
import me.podsialdy.api.Repository.RefreshTokenRepository;

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

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public void initRefreshToken(Customer customer, String sessionId) {

        log.info("Init refresh token for {}", customer.getUsername());

        UUID session = UUID.fromString(sessionId);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findBySessionId(session);

        if (refreshToken.isEmpty() || refreshToken.get().getExpiration().isBefore(Instant.now())) {

            log.info("Create refresh token for customer {}", customer.getUsername());

            String token = jwtService.generateToken(customer);

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .customer(customer)
                    .sessionId(session)
                    .token(token)
                    .build();

            refreshTokenRepository.save(newRefreshToken);
        }

    }

}
