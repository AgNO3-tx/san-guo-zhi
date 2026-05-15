package member4_battlefield;

import java.util.*;

/**
 * 火烧赤壁 - 战船集群统计
 * 输入2D矩阵（1=战船，0=空地）
 * 8连通算一个集群
 * 统计总集群数（决定火球数量）
 */
public class FireAttack {

    private int rows, cols;
    private int[][] grid;
    private boolean[][] visited;

    // 8连通方向：上、下、左、右、左上、右上、左下、右下
    private int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
    private int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

    public FireAttack() {
    }

    /**
     * 统计8连通战船集群数
     * @param grid 2D矩阵
     * @return 集群数量
     */
    public int countClusters(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.visited = new boolean[rows][cols];

        int clusterCount = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1 && !visited[r][c]) {
                    clusterCount++;
                    dfs(r, c);
                }
            }
        }

        return clusterCount;
    }

    /**
     * DFS遍历连通区域
     */
    private void dfs(int r, int c) {
        visited[r][c] = true;

        for (int i = 0; i < 8; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && grid[nr][nc] == 1 && !visited[nr][nc]) {
                dfs(nr, nc);
            }
        }
    }

    /**
     * 获取每个集群的详细信息
     */
    public List<Set<String>> getClusterDetails(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.visited = new boolean[rows][cols];

        List<Set<String>> clusters = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1 && !visited[r][c]) {
                    Set<String> cluster = new HashSet<>();
                    dfsCollect(r, c, cluster);
                    clusters.add(cluster);
                }
            }
        }

        return clusters;
    }

    private void dfsCollect(int r, int c, Set<String> cluster) {
        visited[r][c] = true;
        cluster.add(r + "," + c);

        for (int i = 0; i < 8; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && grid[nr][nc] == 1 && !visited[nr][nc]) {
                dfsCollect(nr, nc, cluster);
            }
        }
    }

    /**
     * 打印矩阵和集群信息
     */
    public void printAnalysis(int[][] grid) {
        int count = countClusters(grid);

        System.out.println("\n🔥 火烧赤壁 - 战船集群统计");
        System.out.println("=".repeat(60));
        System.out.println("战船分布图（1=战船，0=空地）：");
        printGrid(grid);
        System.out.println("\n8连通集群数量：" + count);
        System.out.println("所需火球数量：" + count);

        // 打印每个集群详情
        List<Set<String>> clusters = getClusterDetails(grid);
        System.out.println("-".repeat(60));
        for (int i = 0; i < clusters.size(); i++) {
            System.out.printf("集群%d：%d艘战船\n", i + 1, clusters.get(i).size());
        }
        System.out.println("=".repeat(60));
    }

    private void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int val : row) {
                System.out.print(val == 1 ? " ⛵" : " ·");
            }
            System.out.println();
        }
    }

    /**
     * 最优火球投放点（拓展功能）
     * 找到能让每个战船集群最快被烧完的投放点
     * 策略：选择距离所有集群中心点最近的位置
     */
    public void findOptimalFirePoint(int[][] grid) {
        List<Set<String>> clusters = getClusterDetails(grid);

        System.out.println("\n🔥 最优火球投放点分析（拓展功能）");
        System.out.println("=".repeat(60));

        if (clusters.isEmpty()) {
            System.out.println("⚠ 没有战船集群！");
            return;
        }

        // 计算每个集群的中心点
        List<int[]> centers = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            int sumR = 0, sumC = 0;
            for (String pos : clusters.get(i)) {
                String[] parts = pos.split(",");
                sumR += Integer.parseInt(parts[0]);
                sumC += Integer.parseInt(parts[1]);
            }
            int centerR = sumR / clusters.get(i).size();
            int centerC = sumC / clusters.get(i).size();
            centers.add(new int[]{centerR, centerC});
            System.out.printf("集群%d中心：(%d, %d)，%d艘战船\n",
                    i + 1, centerR, centerC, clusters.get(i).size());
        }

        // 寻找最优投放点：使得到所有集群中心的最大曼哈顿距离最小化
        int bestR = 0, bestC = 0;
        int minMaxDist = Integer.MAX_VALUE;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int maxDist = 0;
                for (int[] center : centers) {
                    int dist = Math.abs(r - center[0]) + Math.abs(c - center[1]);
                    maxDist = Math.max(maxDist, dist);
                }
                if (maxDist < minMaxDist) {
                    minMaxDist = maxDist;
                    bestR = r;
                    bestC = c;
                }
            }
        }

        System.out.println("-".repeat(60));
        System.out.printf("🏆 最优火球投放点：(%d, %d)\n", bestR, bestC);
        System.out.printf("到最远集群的距离：%d\n", minMaxDist);
        System.out.println("=".repeat(60));
    }

    /**
     * 运行默认示例
     */
    public void runDefault() {
        int[][] grid = {
                {1, 1, 0, 0, 0, 1, 1},
                {1, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1, 1},
                {1, 1, 0, 0, 0, 1, 1}
        };
        printAnalysis(grid);
        findOptimalFirePoint(grid);
    }
}
