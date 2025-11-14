package dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AnswerDto {

    @NotBlank(message = "{notBlank}")
    @Size(max = 72, message = "{size.tooLong}")
    private String text;

}
