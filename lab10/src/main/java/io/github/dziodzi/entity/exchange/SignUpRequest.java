package io.github.dziodzi.entity.exchange;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Registration request")
@Builder
@AllArgsConstructor
public class SignUpRequest {
    
    @Schema(description = "Username", example = "Zelibobka")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    @Schema(description = "Email address", example = "zeli.bobka@pochta.ru")
    @Size(min = 5, max = 255, message = "Email address must be between 5 and 255 characters")
    @NotBlank(message = "Email address cannot be blank")
    @Email(message = "Email address must be in the format user@example.com")
    private String email;
    
    @Schema(description = "Password", example = "LilPassword15")
    @Size(max = 255, message = "Password length must not exceed 255 characters")
    private String password;
}
