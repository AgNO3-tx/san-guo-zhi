package sunwu.domain;

import java.util.List;
import java.util.Map;

/**
 * 普通战场图，使用邻接表存储无权图连接关系。
 */
public record BattlefieldGraph(
    Map<Integer, List<Integer>> adjacency
) {
}
