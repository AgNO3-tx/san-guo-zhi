package member3_graph;

import java.util.*;

/**
 * 图结构 - 使用邻接表表示有向图
 * 用于敌营进攻路径搜索和粮草征收路径
 */
public class Graph {
    private int numNodes;
    private Map<Integer, List<Integer>> adjList;

    public Graph(int numNodes) {
        this.numNodes = numNodes;
        this.adjList = new HashMap<>();
        for (int i = 1; i <= numNodes; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    /**
     * 添加有向边
     */
    public void addEdge(int from, int to) {
        adjList.get(from).add(to);
    }

    /**
     * 添加无向边
     */
    public void addUndirectedEdge(int from, int to) {
        adjList.get(from).add(to);
        adjList.get(to).add(from);
    }

    /**
     * 获取邻接表
     */
    public Map<Integer, List<Integer>> getAdjList() {
        return adjList;
    }

    /**
     * 获取节点数
     */
    public int getNumNodes() {
        return numNodes;
    }

    /**
     * 获取某节点的邻居
     */
    public List<Integer> getNeighbors(int node) {
        return adjList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * 打印图结构
     */
    public void printGraph() {
        System.out.println("\n📊 地图结构（邻接表）：");
        System.out.println("=".repeat(50));
        for (int i = 1; i <= numNodes; i++) {
            System.out.printf("Node %d → %s\n", i, adjList.get(i));
        }
        System.out.println("=".repeat(50));
    }

    /**
     * 构建赤壁之战默认地图
     * 有向图，起点Node 1
     */
    public static Graph buildDefaultMap() {
        Graph graph = new Graph(10);
        // Node 1 (起点) → 2, 3
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        // Node 2 → 4, 5
        graph.addEdge(2, 4);
        graph.addEdge(2, 5);
        // Node 3 → 5, 6
        graph.addEdge(3, 5);
        graph.addEdge(3, 6);
        // Node 4 → 7
        graph.addEdge(4, 7);
        // Node 5 → 7, 8
        graph.addEdge(5, 7);
        graph.addEdge(5, 8);
        // Node 6 → 8, 9
        graph.addEdge(6, 8);
        graph.addEdge(6, 9);
        // Node 7 → 10 (敌营)
        graph.addEdge(7, 10);
        // Node 8 → 10 (敌营)
        graph.addEdge(8, 10);
        // Node 9 → 10 (敌营)
        graph.addEdge(9, 10);
        return graph;
    }

    /**
     * 构建粮草征收地图（无向图，带粮草信息）
     */
    public static Graph buildGrainMap() {
        Graph graph = new Graph(8);
        // 无向边
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(1, 3);
        graph.addUndirectedEdge(2, 4);
        graph.addUndirectedEdge(2, 5);
        graph.addUndirectedEdge(3, 5);
        graph.addUndirectedEdge(3, 6);
        graph.addUndirectedEdge(4, 7);
        graph.addUndirectedEdge(5, 7);
        graph.addUndirectedEdge(5, 8);
        graph.addUndirectedEdge(6, 8);
        graph.addUndirectedEdge(7, 8);
        return graph;
    }

    /**
     * 获取默认粮草分布
     */
    public static Map<Integer, Integer> getDefaultGrainMap() {
        Map<Integer, Integer> grain = new HashMap<>();
        grain.put(1, 0);   // 起点无粮草
        grain.put(2, 50);
        grain.put(3, 30);
        grain.put(4, 0);   // 无粮草节点
        grain.put(5, 80);
        grain.put(6, 60);
        grain.put(7, 40);
        grain.put(8, 0);   // 无粮草节点
        return grain;
    }
}
