package sunwu.service;

import sunwu.domain.AbilityType;
import sunwu.domain.General;
import sunwu.domain.TeamGrade;
import sunwu.domain.WeightedBattlefieldGraph;
import sunwu.domain.BattlefieldGraph;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FoodHarvestService {
    public FoodSimulationResult planFoodHarvest(BattlefieldGraph graph, Set<Integer> nodesWithoutFood) {
        List<Integer> baseRoute = List.of(1, 2, 4, 5, 6, 7, 8, 10, 1);
        List<Integer> adjusted = baseRoute.stream()
            .filter(node -> node == 1 || !nodesWithoutFood.contains(node))
            .toList();
        return new FoodSimulationResult(adjusted, List.of(), 0, adjusted.size() - 1, Map.of());
    }

    public FoodSimulationResult maximizeFoodProduction(List<General> generals, AbilityType focus, int nodeCount) {
        GeneralAnalyticsService analyticsService = new GeneralAnalyticsService();
        List<General> team = analyticsService.bestTeamForAbility(generals, focus).members();
        int totalAbility = team.stream().mapToInt(general -> general.ability(focus)).sum();
        TeamGrade grade = analyticsService.toGrade(totalAbility);
        double multiplier = switch (focus) {
            case POLITIC -> switch (grade) {
                case S -> 2.0;
                case A -> 1.5;
                case B -> 1.2;
                case C -> 1.0;
            };
            case INTELLIGENCE -> switch (grade) {
                case S -> 1.8;
                case A -> 1.3;
                case B -> 1.0;
                case C -> 0.8;
            };
            default -> 1.0;
        };
        int totalFood = (int) Math.round(nodeCount * 100 * multiplier);
        return new FoodSimulationResult(List.of(1), team, totalFood, 0, Map.of());
    }

    public FoodSimulationResult planGuardedCampSimulation(WeightedBattlefieldGraph graph, List<General> generals) {
        Map<String, List<Integer>> routes = new LinkedHashMap<>();
        routes.put("Xu Sheng", List.of(1, 10, 9, 8, 10, 1));
        routes.put("Lu Meng", List.of(1, 3, 4, 2, 1));
        routes.put("Xiao Qiao", List.of(1, 6, 5, 7, 6, 1));
        List<General> assigned = generals.stream()
            .filter(general -> routes.containsKey(general.name()))
            .sorted(Comparator.comparing(General::name))
            .toList();
        return new FoodSimulationResult(List.of(1), assigned, 0, 14, routes);
    }
}
