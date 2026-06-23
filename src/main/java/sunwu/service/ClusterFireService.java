package sunwu.service;

import sunwu.domain.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 火攻矩阵服务。
 * 使用 8 连通规则统计战船集群，并为每个集群寻找较优投掷点。
 */
public final class ClusterFireService {
    // 八个方向：上下左右加四个对角线，符合题目中的连锁燃烧规则。
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    /**
     * 统计矩阵中 1 组成的 8 连通集群数量。
     */
    public ClusterAnalysisResult countClusters(int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        int clusterCount = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 1 && !visited[row][col]) {
                    clusterCount++;
                    // 每次从未访问战船出发扩散，就找到了一个新集群。
                    flood(grid, visited, row, col);
                }
            }
        }
        return new ClusterAnalysisResult(clusterCount, List.of());
    }

    /**
     * 对每个集群分别寻找投掷点。
     */
    public ClusterAnalysisResult findOptimalIgnitionPoints(int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        List<Point> points = new ArrayList<>();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 1 && !visited[row][col]) {
                    List<Point> cluster = new ArrayList<>();
                    collectCluster(grid, visited, row, col, cluster);
                    points.add(findClusterCenter(cluster));
                }
            }
        }
        return new ClusterAnalysisResult(points.size(), points);
    }

    /**
     * 只关心标记访问状态的 BFS 扩散。
     */
    private void flood(int[][] grid, boolean[][] visited, int row, int col) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(row, col));
        visited[row][col] = true;

        while (!queue.isEmpty()) {
            Point point = queue.remove();
            for (int[] direction : DIRECTIONS) {
                int nextRow = point.row() + direction[0];
                int nextCol = point.col() + direction[1];
                if (isValid(grid, visited, nextRow, nextCol)) {
                    visited[nextRow][nextCol] = true;
                    queue.add(new Point(nextRow, nextCol));
                }
            }
        }
    }

    /**
     * 收集一个集群内的所有坐标，供后续计算中心点。
     */
    private void collectCluster(int[][] grid, boolean[][] visited, int row, int col, List<Point> cluster) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(row, col));
        visited[row][col] = true;

        while (!queue.isEmpty()) {
            Point point = queue.remove();
            cluster.add(point);
            for (int[] direction : DIRECTIONS) {
                int nextRow = point.row() + direction[0];
                int nextCol = point.col() + direction[1];
                if (isValid(grid, visited, nextRow, nextCol)) {
                    visited[nextRow][nextCol] = true;
                    queue.add(new Point(nextRow, nextCol));
                }
            }
        }
    }

    /**
     * 在集群内部选择最大扩散距离最小的点，等价于尽量缩短烧完整个集群的轮数。
     */
    private Point findClusterCenter(List<Point> cluster) {
        Point best = cluster.getFirst();
        int bestDistance = Integer.MAX_VALUE;
        for (Point candidate : cluster) {
            int eccentricity = 0;
            for (Point point : cluster) {
                // 8 方向扩散下，一轮能同时覆盖行列各一步，因此使用 Chebyshev 距离。
                int distance = Math.max(Math.abs(candidate.row() - point.row()), Math.abs(candidate.col() - point.col()));
                eccentricity = Math.max(eccentricity, distance);
            }
            if (eccentricity < bestDistance) {
                bestDistance = eccentricity;
                best = candidate;
            }
        }
        return best;
    }

    /**
     * 判断坐标是否在矩阵内、是战船、且还没访问过。
     */
    private boolean isValid(int[][] grid, boolean[][] visited, int row, int col) {
        return row >= 0 && row < grid.length
            && col >= 0 && col < grid[0].length
            && grid[row][col] == 1
            && !visited[row][col];
    }
}
