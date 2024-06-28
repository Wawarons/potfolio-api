package me.podsialdy.api.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.podsialdy.api.Entity.RefreshToken;

/**
 * RefreshTokenRepository
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findBySessionId(UUID sessionId);
    
}
