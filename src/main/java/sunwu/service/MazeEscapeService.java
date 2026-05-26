package sunwu.service;

import sunwu.domain.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class MazeEscapeService {
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public MazeEscapeResult escape(int[][] maze) {
        Point start = null;
        Point exit = null;
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                if (maze[row][col] == 2) {
                    start = new Point(row, col);
                }
                if (maze[row][col] == 3) {
                    exit = new Point(row, col);
                }
            }
        }

        Point[][] previous = new Point[maze.length][maze[0].length];
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.row()][start.col()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.remove();
            if (current.equals(exit)) {
                break;
            }
            for (int[] direction : DIRECTIONS) {
                int nextRow = current.row() + direction[0];
                int nextCol = current.col() + direction[1];
                if (nextRow >= 0 && nextRow < maze.length
                    && nextCol >= 0 && nextCol < maze[0].length
                    && maze[nextRow][nextCol] != 1
                    && !visited[nextRow][nextCol]) {
                    visited[nextRow][nextCol] = true;
                    previous[nextRow][nextCol] = current;
                    queue.add(new Point(nextRow, nextCol));
                }
            }
        }

        List<Point> path = new ArrayList<>();
        Point current = exit;
        while (current != null) {
            path.add(0, current);
            current = previous[current.row()][current.col()];
        }
        return new MazeEscapeResult(start, exit, path);
    }
}
