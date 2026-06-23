package sunwu.service;

import java.util.List;

/**
 * 图路径搜索结果，paths 中可包含多条同样最短的路径。
 */
public record PathResult(
    List<List<Integer>> paths
) {
}
