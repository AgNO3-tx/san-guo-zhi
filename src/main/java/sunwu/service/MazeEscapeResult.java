package sunwu.service;

import sunwu.domain.Point;

import java.util.List;

public record MazeEscapeResult(
    Point start,
    Point exit,
    List<Point> path
) {
}
