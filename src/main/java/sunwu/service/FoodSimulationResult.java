package sunwu.service;

import sunwu.domain.General;

import java.util.List;
import java.util.Map;

public record FoodSimulationResult(
    List<Integer> path,
    List<General> assignedTeam,
    int totalFood,
    int totalCost,
    Map<String, List<Integer>> generalRoutes
) {
}
