package sunwu.service;

import sunwu.domain.Point;

import java.util.List;

/**
 * 火攻分析结果：集群数量和每个集群推荐的投掷点。
 */
public record ClusterAnalysisResult(
    int clusterCount,
    List<Point> optimalIgnitionPoints
) {
}
