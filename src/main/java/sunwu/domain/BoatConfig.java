package sunwu.domain;

import java.util.Map;

public record BoatConfig(
    Map<String, Integer> strawMenByDirection
) {
}
