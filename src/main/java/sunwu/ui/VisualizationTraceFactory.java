package sunwu.ui;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.ArrowPlanResult;
import sunwu.domain.BattlefieldGraph;
import sunwu.domain.Edge;
import sunwu.domain.General;
import sunwu.domain.Point;
import sunwu.domain.TerrainType;
import sunwu.domain.WeightedBattlefieldGraph;
import sunwu.service.ArrowBorrowingService;
import sunwu.service.BattlefieldPathService;
import sunwu.service.FoodHarvestService;
import sunwu.service.FoodSimulationResult;
import sunwu.service.GeneralAnalyticsService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * 为 Swing 演示界面生成可播放的算法步骤。
 */
public final class VisualizationTraceFactory {
    private static final int[][] GRID_DIRECTIONS_4 = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    private static final int[][] GRID_DIRECTIONS_8 = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    private VisualizationTraceFactory() {
    }

    public static VisualizationTrace fortressBfsTrace(int target) {
        BattlefieldGraph graph = SampleData.battlefieldGraph();
        List<VisualizationStep> steps = new ArrayList<>();
        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> distance = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new LinkedHashSet<>();

        queue.add(1);
        distance.put(1, 0);
        steps.add(VisualizationStep.builder("BFS 初始化", "从 Node 1 出发，将 Node 1 加入队列。")
            .currentNode(1)
            .visitedNodes(visited)
            .build());

        while (!queue.isEmpty()) {
            int node = queue.remove();
            visited.add(node);
            steps.add(VisualizationStep.builder("访问节点", "取出队首 Node " + node + "，检查它的相邻节点。")
                .currentNode(node)
                .visitedNodes(visited)
                .build());
            if (node == target) {
                break;
            }

            for (int neighbor : sortedNeighbors(graph.adjacency().getOrDefault(node, List.of()))) {
                if (!distance.containsKey(neighbor)) {
                    distance.put(neighbor, distance.get(node) + 1);
                    parent.put(neighbor, node);
                    queue.add(neighbor);
                    steps.add(VisualizationStep.builder("邻居入队", "从 Node " + node + " 发现 Node " + neighbor + "，加入队列。")
                        .currentNode(node)
                        .candidateNode(neighbor)
                        .visitedNodes(visited)
                        .edge(node, neighbor)
                        .build());
                } else {
                    steps.add(VisualizationStep.builder("跳过已发现节点", "Node " + neighbor + " 已经有更早的访问记录，本轮不重复入队。")
                        .currentNode(node)
                        .candidateNode(neighbor)
                        .visitedNodes(visited)
                        .edge(node, neighbor)
                        .build());
                }
            }
        }

        List<List<Integer>> servicePaths = new BattlefieldPathService()
            .findShortestPaths(graph, 1, target)
            .paths();
        List<Integer> path = servicePaths.isEmpty() ? rebuildNodePath(parent, 1, target) : servicePaths.getFirst();
        steps.add(VisualizationStep.builder("回溯路径", "BFS 完成，按父节点回溯得到最终路径。")
            .visitedNodes(visited)
            .pathNodes(path)
            .build());

        return VisualizationTrace.graph("Fortress Attack BFS", battlefieldPositions(), battlefieldEdges(), steps);
    }

    public static VisualizationTrace foodHarvestTrace(Set<Integer> nodesWithoutFood) {
        List<Integer> route = new FoodHarvestService()
            .planFoodHarvest(SampleData.battlefieldGraph(), nodesWithoutFood)
            .path();
        List<Integer> accepted = new ArrayList<>();
        Set<Integer> visited = new LinkedHashSet<>();
        List<VisualizationStep> steps = new ArrayList<>();

        for (int node : route) {
            visited.add(node);
            if (node != 1 && nodesWithoutFood.contains(node)) {
                accepted.add(node);
                steps.add(VisualizationStep.builder("经过连接节点", "Node " + node + " 没有粮草，但需要经过它来连接完整路线。")
                    .currentNode(node)
                    .visitedNodes(visited)
                    .pathNodes(accepted)
                    .build());
            } else {
                accepted.add(node);
                steps.add(VisualizationStep.builder("保留路线节点", "Node " + node + " 可以进入粮草路线，连接到当前路径。")
                    .currentNode(node)
                    .visitedNodes(visited)
                    .pathNodes(accepted)
                    .build());
            }
        }

        steps.add(VisualizationStep.builder("粮草路线完成", "过滤结束，最终路线已高亮。")
            .visitedNodes(visited)
            .pathNodes(accepted)
            .metric("cost", Math.max(0, accepted.size() - 1))
            .build());

        return VisualizationTrace.graph("Food Harvesting Route", battlefieldPositions(), battlefieldEdges(), steps);
    }

    public static VisualizationTrace mazeTrace() {
        int[][] maze = SampleData.huaRongMaze();
        Point start = findCell(maze, 2);
        Point exit = findCell(maze, 3);
        Queue<Point> queue = new ArrayDeque<>();
        Map<Point, Point> previous = new HashMap<>();
        Set<Point> visited = new LinkedHashSet<>();
        List<VisualizationStep> steps = new ArrayList<>();

        queue.add(start);
        visited.add(start);
        steps.add(VisualizationStep.builder("迷宫 BFS 初始化", "曹操从入口开始，起点加入队列。")
            .currentCell(start)
            .visitedCells(visited)
            .build());

        while (!queue.isEmpty()) {
            Point point = queue.remove();
            steps.add(VisualizationStep.builder("扩散当前格", "检查格子 (" + point.row() + "," + point.col() + ") 的四个方向。")
                .currentCell(point)
                .visitedCells(visited)
                .build());
            if (point.equals(exit)) {
                steps.add(VisualizationStep.builder("到达出口", "到达出口，开始回溯逃跑路线。")
                    .currentCell(point)
                    .visitedCells(visited)
                    .pathNodes(List.of())
                    .build());
                break;
            }
            for (int[] direction : GRID_DIRECTIONS_4) {
                Point next = new Point(point.row() + direction[0], point.col() + direction[1]);
                if (isMazeOpen(maze, next) && !visited.contains(next)) {
                    visited.add(next);
                    previous.put(next, point);
                    queue.add(next);
                    steps.add(VisualizationStep.builder("通路入队", "发现可通行格 (" + next.row() + "," + next.col() + ")，加入 BFS 队列。")
                        .currentCell(next)
                        .visitedCells(visited)
                        .build());
                }
            }
        }

        List<Point> path = rebuildCellPath(previous, start, exit);
        steps.add(VisualizationStep.builder("逃跑路线完成", "最终逃跑路线已在迷宫中高亮。")
            .visitedCells(visited)
            .clusterCells(pointsAsCluster(path, 9))
            .build());
        return VisualizationTrace.grid("Hua Rong Road Maze", maze, steps);
    }

    public static VisualizationTrace weightedPathTrace(String generalName, int target) {
        WeightedBattlefieldGraph graph = SampleData.weightedBattlefieldGraph();
        General general = findGeneral(generalName);
        List<VisualizationStep> steps = new ArrayList<>();
        Map<Integer, Double> distance = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> settled = new LinkedHashSet<>();
        record NodeState(int node, double cost) {
        }
        PriorityQueue<NodeState> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeState::cost));

        distance.put(1, 0.0);
        queue.add(new NodeState(1, 0.0));
        steps.add(VisualizationStep.builder("Dijkstra 初始化", general.name() + " 从 Node 1 出发，初始耗时为 0。")
            .currentNode(1)
            .metric("time", 0)
            .build());

        while (!queue.isEmpty()) {
            NodeState state = queue.remove();
            if (state.cost() > distance.getOrDefault(state.node(), Double.POSITIVE_INFINITY)) {
                continue;
            }
            settled.add(state.node());
            steps.add(VisualizationStep.builder("确定当前最短节点", "选择 Node " + state.node() + "，当前累计耗时 " + formatCost(state.cost()) + "。")
                .currentNode(state.node())
                .visitedNodes(settled)
                .metric("time", (int) Math.round(state.cost() * 100))
                .build());
            if (state.node() == target) {
                break;
            }
            for (Edge edge : graph.adjacency().getOrDefault(state.node(), List.of())) {
                double edgeTime = travelTime(general, edge.terrain());
                double nextCost = state.cost() + edgeTime;
                steps.add(VisualizationStep.builder("检查地形边", "Node " + state.node() + " -> Node " + edge.to() + "，地形 " + edge.terrain() + "，本段耗时 " + formatCost(edgeTime) + "。")
                    .currentNode(state.node())
                    .candidateNode(edge.to())
                    .visitedNodes(settled)
                    .edge(state.node(), edge.to())
                    .metric("time", (int) Math.round(nextCost * 100))
                    .build());
                if (nextCost < distance.getOrDefault(edge.to(), Double.POSITIVE_INFINITY)) {
                    distance.put(edge.to(), nextCost);
                    previous.put(edge.to(), state.node());
                    queue.add(new NodeState(edge.to(), nextCost));
                    steps.add(VisualizationStep.builder("更新最短耗时", "更新 Node " + edge.to() + " 的最佳前驱为 Node " + state.node() + "，累计耗时 " + formatCost(nextCost) + "。")
                        .currentNode(state.node())
                        .candidateNode(edge.to())
                        .visitedNodes(settled)
                        .edge(state.node(), edge.to())
                        .metric("time", (int) Math.round(nextCost * 100))
                        .build());
                }
            }
        }

        List<Integer> path = rebuildNodePath(previous, 1, target);
        double total = new BattlefieldPathService().findShortestTimePath(graph, general, target).totalTime();
        steps.add(VisualizationStep.builder("地形最短时间完成", "Dijkstra 完成，最终路径总耗时 " + formatCost(total) + "。")
            .visitedNodes(settled)
            .pathNodes(path)
            .metric("time", (int) Math.round(total * 100))
            .build());
        return VisualizationTrace.graph("Weighted Fortress Terrain Path", battlefieldPositions(), weightedBattlefieldEdges(), steps);
    }

    public static VisualizationTrace fireClusterTrace() {
        int[][] grid = SampleData.simpleFireGrid();
        List<VisualizationStep> steps = new ArrayList<>();
        Map<Point, Integer> clusterCells = new LinkedHashMap<>();
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        int clusterId = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Point point = new Point(row, col);
                if (grid[row][col] != 1) {
                    steps.add(VisualizationStep.builder("扫描空位", "格子 (" + row + "," + col + ") 不是战船，继续扫描。")
                        .currentCell(point)
                        .clusterCells(clusterCells)
                        .build());
                } else if (!visited[row][col]) {
                    clusterId++;
                    collectClusterSteps(grid, visited, point, clusterId, clusterCells, steps);
                }
            }
        }

        steps.add(VisualizationStep.builder("集群统计完成", "共发现 " + clusterId + " 个战船集群，需要 " + clusterId + " 个火球。")
            .clusterCells(clusterCells)
            .metric("clusters", clusterId)
            .build());
        return VisualizationTrace.grid("Red Cliff Fire Clusters", grid, steps);
    }

    public static VisualizationTrace optimizedFireTrace() {
        int[][] grid = SampleData.optimizedFireGrid();
        List<VisualizationStep> steps = new ArrayList<>();
        Map<Point, Integer> clusterCells = new LinkedHashMap<>();
        List<List<Point>> clusters = collectClusters(grid, clusterCells);
        int clusterId = 1;
        for (List<Point> cluster : clusters) {
            Point best = null;
            int bestDistance = Integer.MAX_VALUE;
            for (Point candidate : cluster) {
                int distance = maxChebyshevDistance(candidate, cluster);
                steps.add(VisualizationStep.builder("评估投掷点", "集群 " + clusterId + " 试算 (" + candidate.row() + "," + candidate.col() + ")，最大扩散轮数 " + distance + "。")
                    .currentCell(candidate)
                    .selectedCell(candidate)
                    .clusterCells(clusterCells)
                    .metric("spread", distance)
                    .build());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = candidate;
                }
            }
            steps.add(VisualizationStep.builder("最优投掷点", "集群 " + clusterId + " 选择 (" + best.row() + "," + best.col() + ") 作为最优投掷点。")
                .currentCell(best)
                .selectedCell(best)
                .clusterCells(clusterCells)
                .metric("spread", bestDistance)
                .build());
            clusterId++;
        }
        return VisualizationTrace.grid("Optimized Fire Points", grid, steps);
    }

    public static VisualizationTrace classicBoatTrace(List<Integer> waves) {
        ArrowPlanResult result = new ArrowBorrowingService().planClassicBorrowing(SampleData.classicBoatConfig(), waves);
        return boatTrace("Classic Straw Boats", "原题规则", waves, result);
    }

    public static VisualizationTrace dynamicBoatTrace(List<Integer> waves) {
        ArrowPlanResult result = new ArrowBorrowingService().planDynamicBorrowing(SampleData.dynamicBoatConfig(), waves);
        return boatTrace("Dynamic Straw Boats", "动态规则", waves, result);
    }

    public static VisualizationTrace foodProductionTrace(AbilityType focus, int nodeCount) {
        GeneralAnalyticsService analytics = new GeneralAnalyticsService();
        List<General> team = analytics.bestTeamForAbility(SampleData.generals(), focus).members();
        int totalAbility = team.stream().mapToInt(general -> general.ability(focus)).sum();
        FoodSimulationResult result = new FoodHarvestService().maximizeFoodProduction(SampleData.generals(), focus, nodeCount);
        List<VisualizationStep> steps = new ArrayList<>();
        steps.add(VisualizationStep.builder("比较候选队伍", "按 " + focus + " 选择三人队伍，计算总能力值。")
            .metric("ability", totalAbility)
            .build());
        steps.add(VisualizationStep.builder("选择最高收益队伍", "最终选择 " + names(team) + "，预计粮草 " + result.totalFood() + "。")
            .metric("food", result.totalFood())
            .metric("ability", totalAbility)
            .build());
        return VisualizationTrace.score("Food Production Team", steps);
    }

    public static VisualizationTrace guardedCampTrace() {
        FoodSimulationResult result = new FoodHarvestService().planGuardedCampSimulation(SampleData.weightedBattlefieldGraph(), SampleData.generals());
        List<VisualizationStep> steps = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : result.generalRoutes().entrySet()) {
            List<Integer> partial = new ArrayList<>();
            for (int node : entry.getValue()) {
                partial.add(node);
                steps.add(VisualizationStep.builder("武将路线推进", entry.getKey() + " 行进到 Node " + node + "。")
                    .currentNode(node)
                    .pathNodes(partial)
                    .build());
            }
        }
        steps.add(VisualizationStep.builder("三将占营完成", "三名武将路线展示完毕，总成本 " + result.totalCost() + "。")
            .metric("cost", result.totalCost())
            .build());
        return VisualizationTrace.graph("Guarded Camp Routes", battlefieldPositions(), battlefieldEdges(), steps);
    }

    private static VisualizationTrace boatTrace(String title, String mode, List<Integer> waves, ArrowPlanResult result) {
        List<VisualizationStep> steps = new ArrayList<>();
        int cumulative = 0;
        for (int index = 0; index < result.directions().size(); index++) {
            String direction = result.directions().get(index);
            int gained = result.arrowsReceived().get(index);
            cumulative += gained;
            int incoming = index < waves.size() ? waves.get(index) : 0;
            steps.add(VisualizationStep.builder("第 " + (index + 1) + " 轮箭雨", mode + "：箭雨 " + incoming + "，选择 " + direction + "，获得 " + gained + " 支，累计 " + cumulative + " 支。")
                .boatDirection(direction)
                .totalValue(cumulative)
                .metric("incoming", incoming)
                .metric("gained", gained)
                .build());
        }
        steps.add(VisualizationStep.builder("草船借箭完成", mode + " 完成，累计获得 " + result.totalArrows() + " 支箭。")
            .totalValue(result.totalArrows())
            .build());
        return VisualizationTrace.boat(title, steps);
    }

    private static void collectClusterSteps(int[][] grid, boolean[][] visited, Point start, int clusterId, Map<Point, Integer> clusterCells, List<VisualizationStep> steps) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.row()][start.col()] = true;
        steps.add(VisualizationStep.builder("发现集群 " + clusterId, "从战船格 (" + start.row() + "," + start.col() + ") 开始扩散。")
            .currentCell(start)
            .clusterCells(clusterCells)
            .build());

        while (!queue.isEmpty()) {
            Point point = queue.remove();
            clusterCells.put(point, clusterId);
            steps.add(VisualizationStep.builder("集群 " + clusterId + " 扩散", "标记战船格 (" + point.row() + "," + point.col() + ")。")
                .currentCell(point)
                .clusterCells(clusterCells)
                .build());
            for (int[] direction : GRID_DIRECTIONS_8) {
                Point next = new Point(point.row() + direction[0], point.col() + direction[1]);
                if (isShip(grid, next) && !visited[next.row()][next.col()]) {
                    visited[next.row()][next.col()] = true;
                    queue.add(next);
                }
            }
        }
    }

    private static List<List<Point>> collectClusters(int[][] grid, Map<Point, Integer> clusterCells) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        List<List<Point>> clusters = new ArrayList<>();
        int clusterId = 0;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Point start = new Point(row, col);
                if (isShip(grid, start) && !visited[row][col]) {
                    clusterId++;
                    List<Point> cluster = new ArrayList<>();
                    Queue<Point> queue = new ArrayDeque<>();
                    queue.add(start);
                    visited[row][col] = true;
                    while (!queue.isEmpty()) {
                        Point point = queue.remove();
                        cluster.add(point);
                        clusterCells.put(point, clusterId);
                        for (int[] direction : GRID_DIRECTIONS_8) {
                            Point next = new Point(point.row() + direction[0], point.col() + direction[1]);
                            if (isShip(grid, next) && !visited[next.row()][next.col()]) {
                                visited[next.row()][next.col()] = true;
                                queue.add(next);
                            }
                        }
                    }
                    clusters.add(cluster);
                }
            }
        }
        return clusters;
    }

    private static List<int[]> battlefieldEdges() {
        Set<String> seen = new LinkedHashSet<>();
        List<int[]> edges = new ArrayList<>();
        SampleData.battlefieldGraph().adjacency().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                for (int neighbor : sortedNeighbors(entry.getValue())) {
                    int from = entry.getKey();
                    int min = Math.min(from, neighbor);
                    int max = Math.max(from, neighbor);
                    String key = min + "-" + max;
                    if (seen.add(key)) {
                        edges.add(new int[]{from, neighbor});
                    }
                }
            });
        return edges;
    }

    private static Map<Integer, java.awt.Point> battlefieldPositions() {
        Map<Integer, java.awt.Point> positions = new LinkedHashMap<>();
        positions.put(1, new java.awt.Point(70, 310));
        positions.put(2, new java.awt.Point(145, 215));
        positions.put(3, new java.awt.Point(80, 95));
        positions.put(4, new java.awt.Point(205, 145));
        positions.put(5, new java.awt.Point(345, 60));
        positions.put(6, new java.awt.Point(310, 205));
        positions.put(7, new java.awt.Point(510, 85));
        positions.put(8, new java.awt.Point(450, 260));
        positions.put(9, new java.awt.Point(545, 285));
        positions.put(10, new java.awt.Point(310, 345));
        return positions;
    }

    private static List<int[]> weightedBattlefieldEdges() {
        Set<String> seen = new LinkedHashSet<>();
        List<int[]> edges = new ArrayList<>();
        SampleData.weightedBattlefieldGraph().adjacency().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                for (Edge edge : entry.getValue().stream().sorted(Comparator.comparingInt(Edge::to)).toList()) {
                    int from = entry.getKey();
                    int min = Math.min(from, edge.to());
                    int max = Math.max(from, edge.to());
                    String key = min + "-" + max;
                    if (seen.add(key)) {
                        edges.add(new int[]{from, edge.to()});
                    }
                }
            });
        return edges;
    }

    private static List<Integer> sortedNeighbors(List<Integer> neighbors) {
        return neighbors.stream().sorted().toList();
    }

    private static List<Integer> rebuildNodePath(Map<Integer, Integer> parent, int start, int target) {
        if (start == target) {
            return List.of(start);
        }
        if (!parent.containsKey(target)) {
            return List.of();
        }
        List<Integer> path = new ArrayList<>();
        int current = target;
        path.add(current);
        while (current != start && parent.containsKey(current)) {
            current = parent.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static Point findCell(int[][] grid, int value) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == value) {
                    return new Point(row, col);
                }
            }
        }
        return new Point(0, 0);
    }

    private static boolean isMazeOpen(int[][] grid, Point point) {
        return point.row() >= 0 && point.row() < grid.length
            && point.col() >= 0 && point.col() < grid[0].length
            && grid[point.row()][point.col()] != 1;
    }

    private static boolean isShip(int[][] grid, Point point) {
        return point.row() >= 0 && point.row() < grid.length
            && point.col() >= 0 && point.col() < grid[0].length
            && grid[point.row()][point.col()] == 1;
    }

    private static List<Point> rebuildCellPath(Map<Point, Point> previous, Point start, Point exit) {
        List<Point> path = new ArrayList<>();
        if (!previous.containsKey(exit) && !start.equals(exit)) {
            return path;
        }
        Point current = exit;
        path.add(current);
        while (!current.equals(start)) {
            current = previous.get(current);
            path.add(0, current);
        }
        return path;
    }

    private static Map<Point, Integer> pointsAsCluster(List<Point> points, int clusterId) {
        Map<Point, Integer> cells = new LinkedHashMap<>();
        for (Point point : points) {
            cells.put(point, clusterId);
        }
        return cells;
    }

    private static int maxChebyshevDistance(Point candidate, List<Point> cluster) {
        int distance = 0;
        for (Point point : cluster) {
            distance = Math.max(distance, Math.max(Math.abs(candidate.row() - point.row()), Math.abs(candidate.col() - point.col())));
        }
        return distance;
    }

    private static General findGeneral(String name) {
        return SampleData.generals().stream()
            .filter(general -> general.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseGet(() -> SampleData.generals().stream()
                .filter(general -> general.name().equals("Xu Sheng"))
                .findFirst()
                .orElseThrow());
    }

    private static double travelTime(General general, TerrainType terrainType) {
        return switch (general.armyType()) {
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

    private static String formatCost(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private static String names(List<General> generals) {
        return generals.stream()
            .sorted(Comparator.comparing(General::name))
            .map(General::name)
            .toList()
            .toString();
    }
}
