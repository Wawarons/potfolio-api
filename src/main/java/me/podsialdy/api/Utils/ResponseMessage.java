package me.podsialdy.api.Utils;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.intellij.lang.annotations.RegExp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseMessage {

    @Size(min = 2, max = 150)
    private String message;

    @RegExp(prefix = "[0-9]{3}")
    private String code;

}
