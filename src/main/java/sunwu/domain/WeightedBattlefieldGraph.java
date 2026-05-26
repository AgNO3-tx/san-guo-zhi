package sunwu.domain;

import java.util.List;
import java.util.Map;

public record WeightedBattlefieldGraph(
    Map<Integer, List<Edge>> adjacency
) {
}
