package me.podsialdy.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a data transfer object (DTO) for registering a customer.
 * Includes fields for email, username, and password with validation
 * constraints.
 * <ul>
 * <li>The email field must be a valid email address.</li>
 * <li>The username field must be between 3 and 20 characters in length.</li>
 * <li>The password field must meet the following criteria:
 * <ul>
 * <li>At least one lowercase letter, one uppercase letter, one digit, and one
 * special character</li>
 * <li>Must be between 12 and 250 characters in length</li>
 * <li>Must match the specified regex pattern</li>
 * </ul>
 * </li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterDto {

  @NotNull
  @Email
  private String email;

  @NotNull
  @Size(min = 3, max = 20)
  private String username;

  @NotNull
  @Size(min = 12, max = 250, message = "Password size must be greater between 12 & 250")
  @Pattern(regexp = "(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*[0-9]+)(?=.*[.*$_!\\-+@;:/\\\\|']+)[A-Za-z0-9.*$_!\\-+@;:/\\\\|']+", message = "Password wrong format")
  private String password;

}
