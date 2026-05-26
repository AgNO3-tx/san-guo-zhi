package sunwu.service;

import sunwu.domain.ArmyType;
import sunwu.domain.BattlefieldGraph;
import sunwu.domain.Edge;
import sunwu.domain.General;
import sunwu.domain.TerrainType;
import sunwu.domain.WeightedBattlefieldGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public final class BattlefieldPathService {
    public PathResult findShortestPaths(BattlefieldGraph graph, int start, int target) {
        Map<Integer, Integer> distance = new HashMap<>();
        Map<Integer, List<Integer>> parents = new HashMap<>();
        Queue<Integer> queue = new ArrayDeque<>();

        distance.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            int node = queue.remove();
            for (int next : graph.adjacency().getOrDefault(node, List.of())) {
                if (!distance.containsKey(next)) {
                    distance.put(next, distance.get(node) + 1);
                    parents.put(next, new ArrayList<>(List.of(node)));
                    queue.add(next);
                } else if (distance.get(next) == distance.get(node) + 1) {
                    parents.computeIfAbsent(next, ignored -> new ArrayList<>()).add(node);
                }
            }
        }

        List<List<Integer>> allPaths = new ArrayList<>();
        buildPaths(target, start, parents, new ArrayList<>(), allPaths);
        allPaths.sort(Comparator.<List<Integer>>comparingInt(List::size).thenComparing(List::toString));
        return new PathResult(allPaths);
    }

    private void buildPaths(int node, int start, Map<Integer, List<Integer>> parents, List<Integer> current, List<List<Integer>> result) {
        current.add(0, node);
        if (node == start) {
            result.add(new ArrayList<>(current));
            current.remove(0);
            return;
        }
        for (int parent : parents.getOrDefault(node, List.of())) {
            buildPaths(parent, start, parents, current, result);
        }
        current.remove(0);
    }

    public WeightedPathResult findShortestTimePath(WeightedBattlefieldGraph graph, General general, int target) {
        record NodeState(int node, double cost) {
        }

        PriorityQueue<NodeState> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeState::cost));
        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();

        distance.put(1, 0.0);
        queue.add(new NodeState(1, 0.0));

        while (!queue.isEmpty()) {
            NodeState state = queue.remove();
            if (state.cost() > distance.getOrDefault(state.node(), Double.POSITIVE_INFINITY)) {
                continue;
            }
            for (Edge edge : graph.adjacency().getOrDefault(state.node(), List.of())) {
                double nextCost = state.cost() + travelTime(general.armyType(), edge.terrain());
                if (nextCost < distance.getOrDefault(edge.to(), Double.POSITIVE_INFINITY)) {
                    distance.put(edge.to(), nextCost);
                    previous.put(edge.to(), state.node());
                    queue.add(new NodeState(edge.to(), nextCost));
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        Integer current = target;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        return new WeightedPathResult(path, distance.getOrDefault(target, Double.POSITIVE_INFINITY));
    }

    private double travelTime(ArmyType armyType, TerrainType terrainType) {
        return switch (armyType) {
            case CAVALRY -> switch (terrainType) {
                case FLAT -> 1.0 / 6.0;
                case FOREST -> 1.0 / 1.6;
                case SWAMP -> 1.0 / 0.6;
                case PLANK -> 1.0;
            };
            case ARCHER -> switch (terrainType) {
                case FLAT -> 0.5;
                case FOREST -> 1.0;
                case SWAMP -> 0.4;
                case PLANK -> 2.0;
            };
            case INFANTRY -> switch (terrainType) {
                case FLAT -> 0.5;
                case FOREST -> 0.4;
                case SWAMP -> 1.0;
                case PLANK -> 2.0;
            };
        };
    }
}
