package me.podsialdy.api.Entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>Representation of validation code used to validate user's authentication</p>
 * <p>This code is use for validate user's authentication</p> 
 * 
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "validation_code")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @NotBlank
    @NotEmpty
    @Pattern(regexp = "[0-9]{6}", message = "Code invalid")
    private String code;

    @NotNull
    @ManyToOne
    private Customer customer;

    @NotNull
    @Builder.Default
    private boolean isUsed = false;

    @NotNull
    @Builder.Default
    private Instant expiration = Instant.now().plus(5L, ChronoUnit.MINUTES);

    @NotNull
    @Builder.Default
    private Instant createdAt = Instant.now();

}
