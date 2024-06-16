package me.podsialdy.api.DTO;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {


    @NonNull
    @Size(min = 3)
    private String username;

    @NonNull
    private String password;

    
}
