package me.podsialdy.api.DTO;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.Email;
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
  private String password;


}
