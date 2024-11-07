package io.github.dziodzi.entity.exchange;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Request to reset password")
@AllArgsConstructor
public class ResetPasswordRequest {
    
    @Schema(description = "Username", example = "Zelibobka")
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    @Schema(description = "New password", example = "BigNewPassword15")
    @NotBlank(message = "Password cannot be blank")
    private String newPassword;
    
    @Schema(description = "Confirmation code", example = "0000")
    @NotBlank(message = "Confirmation code cannot be blank")
    private String confirmationCode;
}
