package sunwu.service;

import java.util.List;

public record WeightedPathResult(
    List<Integer> path,
    double totalTime
) {
}
