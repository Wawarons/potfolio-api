package me.podsialdy.api.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RefreshToken
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(unique = true, columnDefinition = "varchar(1000)")
    private String token;

    @NotNull
    @ManyToOne
    private Customer customer;

    @NotNull
    private UUID sessionId;

    @NotNull
    @Builder.Default
    private final Instant expiration = Instant.now().plus(30L, ChronoUnit.DAYS);

    @NotNull
    @Builder.Default
    private boolean isLocked = false;

    @NotNull
    @Builder.Default
    private Instant createdAt = Instant.now();
    
}
