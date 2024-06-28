package me.podsialdy.api.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    private Instant expiration = Instant.now().plus(30L, ChronoUnit.DAYS); // TODO handle expiration properly

    @NotNull
    @Builder.Default
    private boolean isLocked = false;

    @NotNull
    @Builder.Default
    private Instant createdAt = Instant.now();

    // id => UUID 
    // Token => Username, scope = "_refresh_token_" 
    // Customer 
    // expiration => Instant 
    // is locked 
    // Created at => Instant 


    
}
