package me.podsialdy.api.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a data transfer object (DTO) for login information.
 * Includes the username and password fields with validation constraints.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull
    @Size(min = 3)
    private String username;

    @NotNull
    private String password;

}
