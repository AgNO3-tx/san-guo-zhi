package sunwu.service;

import sunwu.domain.Point;

import java.util.List;

/**
 * 华容道逃跑结果：记录起点、出口和 BFS 找到的路径。
 */
public record MazeEscapeResult(
    Point start,
    Point exit,
    List<Point> path
) {
}
