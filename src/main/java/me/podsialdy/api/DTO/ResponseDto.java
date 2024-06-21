package me.podsialdy.api.DTO;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    
    @Pattern(regexp = "[0-9]{3}", message = "Code error invalid")
    private int code;

    @Size(min = 5, message = "Message size must be greater than 5")
    @NotNull
    private String message;

    @Builder.Default
    private Instant timestamp = Instant.now(); 

    public ResponseDto(String message, int code) {
        this.message = message;
        this.code = code;
        this.timestamp = Instant.now();
    }

}

