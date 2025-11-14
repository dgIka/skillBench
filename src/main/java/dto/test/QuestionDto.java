package dto.test;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionDto {

    @NotBlank(message = "{notBlank}")
    @Size(max = 72, message = "{size.tooLong}")
    private String text;

    @Size(min = 4, max = 4)
    private List<AnswerDto> answers;

    @Min(value = 0, message = "{int.size.tooShort}")
    @Max(value = 3, message = "{int.size.tooLong}")
    private int correctIndex;

}
