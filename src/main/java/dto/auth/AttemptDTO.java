package dto.auth;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class AttemptDTO {
    private final int testId;

    private final Map<Integer, Integer> choices = new HashMap<>();

    private int trueCount;

    private int falseCount;

    private boolean completed;

}
