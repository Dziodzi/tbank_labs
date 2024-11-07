package io.github.dziodzi.entity.exchange;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Authentication request")
@Builder
@AllArgsConstructor
public class SignInRequest {
    
    @Schema(description = "Username", example = "")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    @Schema(description = "Password", example = "LilPassword15")
    @Size(min = 8, max = 255, message = "Password length must be between 8 and 255 characters")
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @Schema(description = "Remember me flag", example = "true")
    private Boolean rememberMe;
    
    public Boolean isRememberMe() {
        return rememberMe != null ? rememberMe : Boolean.FALSE;
    }
}
