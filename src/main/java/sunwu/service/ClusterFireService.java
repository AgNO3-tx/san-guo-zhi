package sunwu.service;

import sunwu.domain.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class ClusterFireService {
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    public ClusterAnalysisResult countClusters(int[][] grid) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        int clusterCount = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 1 && !visited[row][col]) {
                    clusterCount++;
                    flood(grid, visited, row, col);
                }
            }
        }
        return new ClusterAnalysisResult(clusterCount, List.of());
    }

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

    private Point findClusterCenter(List<Point> cluster) {
        Point best = cluster.getFirst();
        int bestDistance = Integer.MAX_VALUE;
        for (Point candidate : cluster) {
            int eccentricity = 0;
            for (Point point : cluster) {
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

    private boolean isValid(int[][] grid, boolean[][] visited, int row, int col) {
        return row >= 0 && row < grid.length
            && col >= 0 && col < grid[0].length
            && grid[row][col] == 1
            && !visited[row][col];
    }
}
