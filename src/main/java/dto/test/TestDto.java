package dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TestDto {

    @NotBlank(message = "{notBlank}")
    @Size(max = 72, message = "{size.tooLong}")
    private String name;

    @NotBlank(message = "{notBlank}")
    @Size(max = 72, message = "{size.tooLong}")
    private String theme;
}
