package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.General;
import sunwu.service.BattlefieldPathService;
import sunwu.service.PathResult;
import sunwu.service.WeightedPathResult;

import java.util.List;

/**
 * 验证战场图的 BFS 路径和带地形最短时间路径。
 */
public final class BattlefieldPathServiceTest {
    private BattlefieldPathServiceTest() {
    }

    public static void run() {
        BattlefieldPathService service = new BattlefieldPathService();
        // 默认图中到 8 的两条最短路径应同时保留下来。
        PathResult shortest = service.findShortestPaths(SampleData.battlefieldGraph(), 1, 8);
        TestSupport.assertTrue(shortest.paths().contains(List.of(1, 6, 8)), "Expected path 1->6->8 should exist.");
        TestSupport.assertTrue(shortest.paths().contains(List.of(1, 10, 8)), "Expected path 1->10->8 should exist.");

        General xuSheng = SampleData.generals().stream()
            .filter(g -> g.name().equals("Xu Sheng"))
            .findFirst()
            .orElseThrow();
        WeightedPathResult weighted = service.findShortestTimePath(SampleData.weightedBattlefieldGraph(), xuSheng, 8);
        // Dijkstra 结果至少要保证起点、终点和正成本正确。
        TestSupport.assertEquals(1, weighted.path().getFirst(), "Weighted path should start from node 1.");
        TestSupport.assertEquals(8, weighted.path().getLast(), "Weighted path should end at target node.");
        TestSupport.assertTrue(weighted.totalTime() > 0.0, "Weighted path should produce positive travel time.");
    }
}
