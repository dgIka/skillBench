package dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegisterDto {
    @NotBlank
    @Email
    @Size(max = 256)
    private String email;

    @NotBlank
    @Size(min = 2, max = 72)
    private String password;

    @NotBlank
    @Size(min = 2, max = 72)
    private String password2;

    @NotBlank
    @Size(min = 6, max = 256)
    private String name;
}
