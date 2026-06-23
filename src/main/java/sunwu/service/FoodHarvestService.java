package sunwu.service;

import sunwu.domain.AbilityType;
import sunwu.domain.General;
import sunwu.domain.TeamGrade;
import sunwu.domain.WeightedBattlefieldGraph;
import sunwu.domain.BattlefieldGraph;
import sunwu.domain.Edge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 粮草相关功能服务。
 * 当前实现覆盖基础粮草路线搜索、产量增益和三将占营最小成本模拟。
 */
public final class FoodHarvestService {
    private static final List<String> GUARDED_CAMP_GENERALS = List.of("Xu Sheng", "Lu Meng", "Xiao Qiao");

    /**
     * 基础粮草采集：从 Node 1 出发，覆盖所有有粮节点后回到 Node 1。
     */
    public FoodSimulationResult planFoodHarvest(BattlefieldGraph graph, Set<Integer> nodesWithoutFood) {
        Set<Integer> requiredFoodNodes = new HashSet<>(graph.adjacency().keySet());
        requiredFoodNodes.remove(1);
        requiredFoodNodes.removeAll(nodesWithoutFood);

        List<Integer> bestPath = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        visited.add(1);
        searchFoodRoute(graph, requiredFoodNodes, 1, visited, new ArrayList<>(List.of(1)), bestPath);
        return new FoodSimulationResult(bestPath, List.of(), 0, Math.max(0, bestPath.size() - 1), Map.of());
    }

    private void searchFoodRoute(
        BattlefieldGraph graph,
        Set<Integer> requiredFoodNodes,
        int current,
        Set<Integer> visited,
        List<Integer> path,
        List<Integer> bestPath
    ) {
        if (visited.containsAll(requiredFoodNodes) && graph.adjacency().getOrDefault(current, List.of()).contains(1)) {
            List<Integer> completed = new ArrayList<>(path);
            completed.add(1);
            if (isBetterRoute(completed, bestPath)) {
                bestPath.clear();
                bestPath.addAll(completed);
            }
            return;
        }
        if (!bestPath.isEmpty() && path.size() + 1 >= bestPath.size()) {
            return;
        }

        for (int next : graph.adjacency().getOrDefault(current, List.of()).stream().sorted().toList()) {
            if (next == 1 || visited.contains(next)) {
                continue;
            }
            visited.add(next);
            path.add(next);
            searchFoodRoute(graph, requiredFoodNodes, next, visited, path, bestPath);
            path.remove(path.size() - 1);
            visited.remove(next);
        }
    }

    private boolean isBetterRoute(List<Integer> candidate, List<Integer> currentBest) {
        if (currentBest.isEmpty() || candidate.size() < currentBest.size()) {
            return true;
        }
        if (candidate.size() > currentBest.size()) {
            return false;
        }
        return candidate.toString().compareTo(currentBest.toString()) < 0;
    }

    /**
     * 粮草采集 I：选择指定能力最高的三人队伍，并按等级倍率计算产量。
     */
    public FoodSimulationResult maximizeFoodProduction(List<General> generals, AbilityType focus, int nodeCount) {
        GeneralAnalyticsService analyticsService = new GeneralAnalyticsService();
        List<General> team = analyticsService.bestTeamForAbility(generals, focus).members();
        int totalAbility = team.stream().mapToInt(general -> general.ability(focus)).sum();
        TeamGrade grade = analyticsService.toGrade(totalAbility);
        // PDF 中政治队和智力队使用不同的加成表。
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
        return new FoodSimulationResult(List.of(), team, totalFood, 0, Map.of());
    }

    /**
     * 粮草采集 II：枚举三名武将均分营地后的最小总步数模拟。
     */
    public FoodSimulationResult planGuardedCampSimulation(WeightedBattlefieldGraph graph, List<General> generals) {
        List<General> assigned = GUARDED_CAMP_GENERALS.stream()
            .map(name -> generals.stream()
                .filter(general -> general.name().equals(name))
                .findFirst()
                .orElseThrow())
            .toList();
        List<Integer> camps = guardedCampSoldiers().keySet().stream().sorted().toList();
        int baseCampsPerGeneral = camps.size() / assigned.size();
        int extraCamps = camps.size() % assigned.size();
        Map<Integer, List<Integer>> adjacency = simpleAdjacency(graph);
        GuardSearch search = new GuardSearch();

        searchGuardAssignments(
            0,
            assigned,
            camps,
            guardedCampSoldiers(),
            adjacency,
            baseCampsPerGeneral,
            extraCamps,
            new LinkedHashSet<>(),
            new ArrayList<>(),
            search
        );

        Map<String, List<Integer>> routes = new LinkedHashMap<>();
        int totalCost = 0;
        for (GuardRoute route : search.bestRoutes) {
            routes.put(route.general().name(), route.path());
            totalCost += route.cost();
        }
        return new FoodSimulationResult(List.of(), assigned, 0, totalCost, routes);
    }

    private void searchGuardAssignments(
        int generalIndex,
        List<General> generals,
        List<Integer> camps,
        Map<Integer, Integer> soldiers,
        Map<Integer, List<Integer>> adjacency,
        int baseCampsPerGeneral,
        int extraCamps,
        Set<Integer> usedCamps,
        List<GuardRoute> routes,
        GuardSearch search
    ) {
        if (generalIndex == generals.size()) {
            if (usedCamps.size() == camps.size()) {
                int cost = routes.stream().mapToInt(GuardRoute::cost).sum();
                if (search.isBetter(cost, routes)) {
                    search.bestCost = cost;
                    search.bestRoutes = new ArrayList<>(routes);
                }
            }
            return;
        }

        General general = generals.get(generalIndex);
        int targetSize = baseCampsPerGeneral + (generalIndex < extraCamps ? 1 : 0);
        List<Integer> remaining = camps.stream()
            .filter(camp -> !usedCamps.contains(camp))
            .filter(camp -> general.strength() >= soldiers.get(camp))
            .toList();
        for (List<Integer> assignedCamps : combinations(remaining, targetSize)) {
            GuardRoute route = bestGuardRoute(general, assignedCamps, adjacency);
            if (route.cost() == Integer.MAX_VALUE) {
                continue;
            }
            int partialCost = routes.stream().mapToInt(GuardRoute::cost).sum() + route.cost();
            if (partialCost >= search.bestCost) {
                continue;
            }
            usedCamps.addAll(assignedCamps);
            routes.add(route);
            searchGuardAssignments(
                generalIndex + 1,
                generals,
                camps,
                soldiers,
                adjacency,
                baseCampsPerGeneral,
                extraCamps,
                usedCamps,
                routes,
                search
            );
            routes.remove(routes.size() - 1);
            usedCamps.removeAll(assignedCamps);
        }
    }

    private GuardRoute bestGuardRoute(General general, List<Integer> assignedCamps, Map<Integer, List<Integer>> adjacency) {
        GuardRoute best = new GuardRoute(general, List.of(), Integer.MAX_VALUE);
        for (List<Integer> order : permutations(assignedCamps)) {
            List<Integer> path = expandRoute(order, adjacency);
            int cost = path.isEmpty() ? Integer.MAX_VALUE : path.size() - 1;
            GuardRoute candidate = new GuardRoute(general, path, cost);
            if (cost < best.cost() || (cost == best.cost() && path.toString().compareTo(best.path().toString()) < 0)) {
                best = candidate;
            }
        }
        return best;
    }

    private List<Integer> expandRoute(List<Integer> order, Map<Integer, List<Integer>> adjacency) {
        List<Integer> expanded = new ArrayList<>(List.of(1));
        int current = 1;
        for (int next : order) {
            List<Integer> segment = shortestPath(adjacency, current, next);
            if (segment.isEmpty()) {
                return List.of();
            }
            expanded.addAll(segment.subList(1, segment.size()));
            current = next;
        }
        List<Integer> home = shortestPath(adjacency, current, 1);
        if (home.isEmpty()) {
            return List.of();
        }
        expanded.addAll(home.subList(1, home.size()));
        return expanded;
    }

    private List<Integer> shortestPath(Map<Integer, List<Integer>> adjacency, int start, int target) {
        if (start == target) {
            return List.of(start);
        }
        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> previous = new LinkedHashMap<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int node = queue.remove();
            for (int next : adjacency.getOrDefault(node, List.of())) {
                if (visited.contains(next)) {
                    continue;
                }
                visited.add(next);
                previous.put(next, node);
                if (next == target) {
                    List<Integer> path = new ArrayList<>();
                    int current = target;
                    path.add(current);
                    while (current != start) {
                        current = previous.get(current);
                        path.add(0, current);
                    }
                    return path;
                }
                queue.add(next);
            }
        }
        return List.of();
    }

    private Map<Integer, List<Integer>> simpleAdjacency(WeightedBattlefieldGraph graph) {
        Map<Integer, List<Integer>> adjacency = new LinkedHashMap<>();
        graph.adjacency().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> adjacency.put(
                entry.getKey(),
                entry.getValue().stream().map(Edge::to).sorted().toList()
            ));
        return adjacency;
    }

    private List<List<Integer>> combinations(List<Integer> values, int size) {
        List<List<Integer>> result = new ArrayList<>();
        collectCombinations(values, size, 0, new ArrayList<>(), result);
        return result;
    }

    private void collectCombinations(List<Integer> values, int size, int index, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = index; i < values.size(); i++) {
            current.add(values.get(i));
            collectCombinations(values, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private List<List<Integer>> permutations(List<Integer> values) {
        List<List<Integer>> result = new ArrayList<>();
        collectPermutations(values, new boolean[values.size()], new ArrayList<>(), result);
        return result;
    }

    private void collectPermutations(List<Integer> values, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == values.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = 0; i < values.size(); i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            current.add(values.get(i));
            collectPermutations(values, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    private Map<Integer, Integer> guardedCampSoldiers() {
        Map<Integer, Integer> soldiers = new LinkedHashMap<>();
        soldiers.put(2, 9);
        soldiers.put(3, 8);
        soldiers.put(4, 5);
        soldiers.put(5, 3);
        soldiers.put(6, 6);
        soldiers.put(7, 8);
        soldiers.put(8, 3);
        soldiers.put(9, 5);
        soldiers.put(10, 6);
        return soldiers;
    }

    private record GuardRoute(General general, List<Integer> path, int cost) {
    }

    private static final class GuardSearch {
        private int bestCost = Integer.MAX_VALUE;
        private List<GuardRoute> bestRoutes = List.of();

        private boolean isBetter(int cost, List<GuardRoute> routes) {
            if (cost < bestCost) {
                return true;
            }
            if (cost > bestCost) {
                return false;
            }
            return routes.toString().compareTo(bestRoutes.toString()) < 0;
        }
    }
}
