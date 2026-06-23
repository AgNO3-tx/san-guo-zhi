package sunwu.domain;

import java.util.List;
import java.util.Map;

/**
 * 带地形权重信息的战场图，供 Dijkstra 最短时间路径使用。
 */
public record WeightedBattlefieldGraph(
    Map<Integer, List<Edge>> adjacency
) {
}
