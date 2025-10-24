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
    @NotBlank(message = "{auth.email.required}")
    @Email(message = "{email.invalid}")
    @Size(max = 256, message = "{size.tooLong}")
    private String email;

    @NotBlank(message = "{auth.email.required}/")
    @Size(min = 2, message = "{size.tooShort}")
    @Size(max = 72, message = "{size.tooLong}")
    private String password;

    @NotBlank(message = "{auth.email.required}/")
    @Size(min = 2, message = "{size.tooShort}")
    @Size(max = 72, message = "{size.tooLong}")
    private String password2;

    @NotBlank(message = "{notblank}")
    @Size(min = 2, message = "{size.tooShort}")
    @Size(max = 72, message = "{size.tooLong}")
    private String name;
}
