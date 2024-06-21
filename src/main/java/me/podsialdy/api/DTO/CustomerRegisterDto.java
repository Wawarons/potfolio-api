package me.podsialdy.api.DTO;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterDto {

  @NonNull  
  @Email
  private String email;

  @NonNull  
  @Size(min = 3, max = 20)
  private String username;

  @NonNull
  @Pattern(regexp = "(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*[0-9]+)(?=.*[.*$_!\\-+@;:/\\\\|']+)[A-Za-z0-9.*$_!\\-+@;:/\\\\|']+", message = "Password wrong format")
  @Size(min = 12, max = 250, message = "Password size must be greater between 12 and 250")
  private String password;


}
