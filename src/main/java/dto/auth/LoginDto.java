package dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "{auth.email.required}")
    @Email(message = "{email.invalid}")
    @Size(min = 6, message = "{size.tooShort}")
    @Size(max = 256, message = "{size.tooLong}")
    private String email;

    @NotBlank(message = "{auth.password.required}")
    @Size(min = 6, message = "{size.tooShort}")
    @Size(max = 72, message = "{size.tooLong}")
    private String password;
}
