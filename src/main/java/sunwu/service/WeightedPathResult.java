package sunwu.service;

import java.util.List;

/**
 * 带地形最短时间路径结果，包含节点路径和总耗时。
 */
public record WeightedPathResult(
    List<Integer> path,
    double totalTime
) {
}
