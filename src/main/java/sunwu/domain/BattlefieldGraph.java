package sunwu.domain;

import java.util.List;
import java.util.Map;

public record BattlefieldGraph(
    Map<Integer, List<Integer>> adjacency
) {
}
