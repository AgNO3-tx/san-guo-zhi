package sunwu.domain;

import java.util.List;

public record ArrowPlanResult(
    List<String> directions,
    List<Integer> arrowsReceived,
    int totalArrows
) {
}
