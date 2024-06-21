package me.podsialdy.api.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>Data Object Transfer for request a validation code</p>
 * <p>This code must:<p>
 * <ul>
 * <li>Not be blank</li>
 * <li>Not be empty</li>
 * <li>Not be null</li>
 * <li>Contain only six digits</li>
 * </ul>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CodeValidationDto {
 
    @NotNull(message = "Code cannot be null")
    @NotBlank(message = "Code cfannot be blank")
    @NotEmpty(message = "Code invalid")
    @Pattern(regexp = "[0-9]{6}", message = "Code invalid")
    private String code;

}
