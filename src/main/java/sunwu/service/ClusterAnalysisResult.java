package sunwu.service;

import sunwu.domain.Point;

import java.util.List;

public record ClusterAnalysisResult(
    int clusterCount,
    List<Point> optimalIgnitionPoints
) {
}
