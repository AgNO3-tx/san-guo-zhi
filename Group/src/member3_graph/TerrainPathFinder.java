package member3_graph;

import java.util.*;

/**
 * 带地形速度的最短时间路径（拓展功能）
 * 地形类型：平地/森林/沼泽/栈道
 * 兵种速度系数：骑兵/步兵/弓兵
 * 使用Dijkstra算法求最短时间路径
 */
public class TerrainPathFinder {

    public enum Terrain {
        平地(1.0), 森林(2.0), 沼泽(3.0), 栈道(1.5);

        private final double costMultiplier;
        Terrain(double cost) { this.costMultiplier = cost; }
        public double getCost() { return costMultiplier; }
    }

    public enum TroopType {
        骑兵(1.5), 步兵(1.0), 弓兵(1.2);

        private final double speed;
        TroopType(double speed) { this.speed = speed; }
        public double getSpeed() { return speed; }
    }

    private int numNodes;
    private Map<Integer, List<Edge>> adjList;
    private Map<Integer, Terrain> nodeTerrain;

    public TerrainPathFinder(int numNodes) {
        this.numNodes = numNodes;
        this.adjList = new HashMap<>();
        this.nodeTerrain = new HashMap<>();
        for (int i = 1; i <= numNodes; i++) {
            adjList.put(i, new ArrayList<>());
            nodeTerrain.put(i, Terrain.平地);
        }
    }

    static class Edge {
        int to;
        double distance;

        Edge(int to, double distance) {
            this.to = to;
            this.distance = distance;
        }
    }

    public void addEdge(int from, int to, double distance) {
        adjList.get(from).add(new Edge(to, distance));
        adjList.get(to).add(new Edge(from, distance));
    }

    public void setTerrain(int node, Terrain terrain) {
        nodeTerrain.put(node, terrain);
    }

    /**
     * Dijkstra算法求最短时间路径
     * @param start 起点
     * @param end 终点
     * @param troopType 兵种
     * @return 路径和时间
     */
    public PathResult findShortestTimePath(int start, int end, TroopType troopType) {
        double[] minTime = new double[numNodes + 1];
        int[] prev = new int[numNodes + 1];
        boolean[] visited = new boolean[numNodes + 1];
        Arrays.fill(minTime, Double.MAX_VALUE);
        Arrays.fill(prev, -1);
        minTime[start] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(
                Comparator.comparingDouble(a -> minTime[a]));
        pq.add(start);

        while (!pq.isEmpty()) {
            int current = pq.poll();
            if (visited[current]) continue;
            visited[current] = true;

            if (current == end) break;

            for (Edge edge : adjList.get(current)) {
                if (visited[edge.to]) continue;

                // 时间 = 距离 / (兵种速度 / 地形系数)
                double terrainCost = nodeTerrain.get(edge.to).getCost();
                double time = edge.distance / (troopType.getSpeed() / terrainCost);

                if (minTime[current] + time < minTime[edge.to]) {
                    minTime[edge.to] = minTime[current] + time;
                    prev[edge.to] = current;
                    pq.add(edge.to);
                }
            }
        }

        // 重建路径
        List<Integer> path = new ArrayList<>();
        if (minTime[end] == Double.MAX_VALUE) {
            return new PathResult(path, Double.MAX_VALUE);
        }

        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);

        return new PathResult(path, minTime[end]);
    }

    static class PathResult {
        List<Integer> path;
        double totalTime;

        PathResult(List<Integer> path, double totalTime) {
            this.path = path;
            this.totalTime = totalTime;
        }
    }

    /**
     * 打印路径结果
     */
    public void printPath(int start, int end, TroopType troopType) {
        PathResult result = findShortestTimePath(start, end, troopType);

        System.out.println("\n⏱️ 带地形速度的最短时间路径（拓展功能）");
        System.out.println("=".repeat(60));
        System.out.println("起点：Node " + start + " → 终点：Node " + end);
        System.out.println("兵种：" + troopType + "（速度系数：" + troopType.getSpeed() + "）");
        System.out.println("-".repeat(60));

        if (result.path.isEmpty() || result.totalTime == Double.MAX_VALUE) {
            System.out.println("⚠ 无法到达终点！");
        } else {
            System.out.println("最短时间路径：");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < result.path.size(); i++) {
                if (i > 0) sb.append(" → ");
                int node = result.path.get(i);
                sb.append("Node ").append(node);
                sb.append("(").append(nodeTerrain.get(node)).append(")");
            }
            System.out.println("  " + sb.toString());
            System.out.printf("总耗时：%.2f 单位时间\n", result.totalTime);
        }
        System.out.println("=".repeat(60));
    }

    /**
     * 构建默认地形地图
     */
    public static TerrainPathFinder buildDefaultMap() {
        TerrainPathFinder finder = new TerrainPathFinder(8);

        // 设置地形
        finder.setTerrain(1, Terrain.平地);
        finder.setTerrain(2, Terrain.森林);
        finder.setTerrain(3, Terrain.平地);
        finder.setTerrain(4, Terrain.沼泽);
        finder.setTerrain(5, Terrain.森林);
        finder.setTerrain(6, Terrain.栈道);
        finder.setTerrain(7, Terrain.沼泽);
        finder.setTerrain(8, Terrain.平地);

        // 添加边（距离）
        finder.addEdge(1, 2, 10);
        finder.addEdge(1, 3, 8);
        finder.addEdge(2, 4, 12);
        finder.addEdge(2, 5, 15);
        finder.addEdge(3, 5, 10);
        finder.addEdge(3, 6, 14);
        finder.addEdge(4, 7, 8);
        finder.addEdge(5, 7, 6);
        finder.addEdge(5, 8, 12);
        finder.addEdge(6, 8, 10);
        finder.addEdge(7, 8, 5);

        return finder;
    }

    /**
     * 运行默认示例
     */
    public void runDefault() {
        TerrainPathFinder finder = buildDefaultMap();
        // 测试不同兵种
        for (TroopType troop : TroopType.values()) {
            finder.printPath(1, 8, troop);
        }
    }
}
