package member3_graph;

import java.util.*;

/**
 * 粮草征收路径
 * 从Node 1出发，不重复经过节点，收完所有粮食返回起点
 * 支持跳过无粮草节点
 */
public class GrainCollector {

    private Graph graph;
    private Map<Integer, Integer> grainMap;

    public GrainCollector(Graph graph, Map<Integer, Integer> grainMap) {
        this.graph = graph;
        this.grainMap = grainMap;
    }

    /**
     * 寻找粮草征收路径
     * 使用DFS回溯，从起点出发，收集所有有粮草的节点后返回起点
     * @param start 起点
     * @return 合法路径，如果找不到返回空列表
     */
    public List<Integer> collectGrain(int start) {
        // 找出所有有粮草的节点
        List<Integer> grainNodes = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : grainMap.entrySet()) {
            if (entry.getValue() > 0 && entry.getKey() != start) {
                grainNodes.add(entry.getKey());
            }
        }

        System.out.println("\n🌾 粮草征收路径规划");
        System.out.println("=".repeat(60));
        System.out.println("起点：Node " + start);
        System.out.println("有粮草节点：" + grainNodes);
        System.out.println("粮草分布：" + grainMap);
        System.out.println("-".repeat(60));

        // 使用DFS搜索路径
        List<Integer> bestPath = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        visited.add(start);

        dfs(start, start, grainNodes, visited, new ArrayList<>(List.of(start)), bestPath);

        if (!bestPath.isEmpty()) {
            System.out.println("✅ 找到合法征收路径：");
            System.out.println("  " + formatPath(bestPath));
            int totalGrain = calculateTotalGrain(bestPath);
            System.out.println("  总征收粮草：" + totalGrain);
        } else {
            System.out.println("⚠ 未找到能收集所有粮草的路径！");
            // 尝试找部分收集的路径
            findPartialPath(start, grainNodes);
        }

        System.out.println("=".repeat(60));
        return bestPath;
    }

    /**
     * DFS搜索路径
     */
    private void dfs(int current, int start, List<Integer> targets, Set<Integer> visited,
                     List<Integer> currentPath, List<Integer> bestPath) {
        // 如果已经收集了所有目标节点，尝试返回起点
        if (visited.containsAll(targets)) {
            // 检查是否能直接返回起点
            if (graph.getNeighbors(current).contains(start)) {
                List<Integer> completePath = new ArrayList<>(currentPath);
                completePath.add(start);
                if (bestPath.isEmpty() || completePath.size() < bestPath.size()) {
                    bestPath.clear();
                    bestPath.addAll(completePath);
                }
                return;
            }
            // 尝试通过其他节点返回
            for (int neighbor : graph.getNeighbors(current)) {
                if (neighbor == start) {
                    List<Integer> completePath = new ArrayList<>(currentPath);
                    completePath.add(start);
                    if (bestPath.isEmpty() || completePath.size() < bestPath.size()) {
                        bestPath.clear();
                        bestPath.addAll(completePath);
                    }
                    return;
                }
            }
        }

        // 剪枝：如果当前路径已经比最佳路径长，不再搜索
        if (!bestPath.isEmpty() && currentPath.size() >= bestPath.size()) {
            return;
        }

        // 扩展邻居
        for (int neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                currentPath.add(neighbor);
                dfs(neighbor, start, targets, visited, currentPath, bestPath);
                currentPath.remove(currentPath.size() - 1);
                visited.remove(neighbor);
            }
        }
    }

    /**
     * 寻找部分收集路径
     */
    private void findPartialPath(int start, List<Integer> grainNodes) {
        System.out.println("\n尝试寻找部分收集路径...");
        List<Integer> partialPath = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        visited.add(start);
        partialPath.add(start);

        // 贪心：每次去最近的有粮草且未访问的节点
        int current = start;
        int collected = 0;

        while (true) {
            int nextNode = -1;
            for (int neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor) && grainMap.getOrDefault(neighbor, 0) > 0) {
                    nextNode = neighbor;
                    break;
                }
            }
            if (nextNode == -1) break;

            visited.add(nextNode);
            partialPath.add(nextNode);
            collected += grainMap.getOrDefault(nextNode, 0);
            current = nextNode;
        }

        // 尝试返回起点
        if (graph.getNeighbors(current).contains(start)) {
            partialPath.add(start);
            System.out.println("  部分收集路径：" + formatPath(partialPath));
            System.out.println("  收集粮草：" + collected);
        }
    }

    /**
     * 计算路径总粮草
     */
    private int calculateTotalGrain(List<Integer> path) {
        int total = 0;
        for (int node : path) {
            total += grainMap.getOrDefault(node, 0);
        }
        return total;
    }

    /**
     * 格式化路径
     */
    private String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append("Node ").append(path.get(i));
            int grain = grainMap.getOrDefault(path.get(i), 0);
            if (grain > 0) {
                sb.append("(粮草:").append(grain).append(")");
            }
        }
        return sb.toString();
    }

    /**
     * 运行默认示例
     */
    public void runDefault() {
        Graph grainGraph = Graph.buildGrainMap();
        Map<Integer, Integer> grain = Graph.getDefaultGrainMap();
        GrainCollector collector = new GrainCollector(grainGraph, grain);
        collector.collectGrain(1);
    }
}
