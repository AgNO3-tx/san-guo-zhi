package member3_graph;

import java.util.*;

/**
 * BFS广度优先搜索 - 查找所有可达敌营的路径
 * 起点Node 1，终点为敌营节点
 */
public class BFSPathFinder {

    private Graph graph;

    public BFSPathFinder(Graph graph) {
        this.graph = graph;
    }

    /**
     * BFS搜索从起点到所有目标节点的所有路径
     * @param start 起点
     * @param targets 目标节点列表（敌营）
     * @return 所有路径的列表
     */
    public List<List<Integer>> findAllPaths(int start, List<Integer> targets) {
        Set<Integer> targetSet = new HashSet<>(targets);
        List<List<Integer>> allPaths = new ArrayList<>();

        // BFS队列：每个元素是路径
        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> startPath = new ArrayList<>();
        startPath.add(start);
        queue.add(startPath);

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int lastNode = path.get(path.size() - 1);

            // 如果到达目标节点，记录路径
            if (targetSet.contains(lastNode)) {
                allPaths.add(new ArrayList<>(path));
                continue; // 不继续从目标节点扩展
            }

            // 扩展邻居节点
            for (int neighbor : graph.getNeighbors(lastNode)) {
                // 避免环路（简单路径：不重复经过节点）
                if (!path.contains(neighbor)) {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return allPaths;
    }

    /**
     * 打印所有路径
     */
    public void printAllPaths(int start, List<Integer> targets) {
        List<List<Integer>> paths = findAllPaths(start, targets);

        System.out.println("\n🗺️ BFS敌营进攻路径搜索");
        System.out.println("=".repeat(60));
        System.out.println("起点：Node " + start);
        System.out.println("敌营节点：" + targets);
        System.out.println("找到 " + paths.size() + " 条可达路径：");
        System.out.println("-".repeat(60));

        int idx = 1;
        for (List<Integer> path : paths) {
            System.out.printf("路径%d：%s\n", idx++, formatPath(path));
        }

        if (paths.isEmpty()) {
            System.out.println("⚠ 没有找到可达敌营的路径！");
        }

        System.out.println("=".repeat(60));
    }

    /**
     * 格式化路径输出
     */
    private String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append("Node ").append(path.get(i));
        }
        return sb.toString();
    }

    /**
     * 运行默认示例
     */
    public void runDefault() {
        Graph defaultGraph = Graph.buildDefaultMap();
        defaultGraph.printGraph();
        BFSPathFinder finder = new BFSPathFinder(defaultGraph);
        finder.printAllPaths(1, Arrays.asList(10));
    }
}
