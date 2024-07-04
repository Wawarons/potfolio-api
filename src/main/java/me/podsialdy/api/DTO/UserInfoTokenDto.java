package me.podsialdy.api.DTO;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoTokenDto {
    
    @NotNull
    private String token;

    @NotNull
    private Instant timestamp = Instant.now();

}
