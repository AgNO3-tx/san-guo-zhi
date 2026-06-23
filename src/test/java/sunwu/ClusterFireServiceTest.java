package sunwu;

import sunwu.data.SampleData;
import sunwu.service.ClusterAnalysisResult;
import sunwu.service.ClusterFireService;

/**
 * 验证火攻矩阵集群数量和每个集群的最优投掷点数量。
 */
public final class ClusterFireServiceTest {
    private ClusterFireServiceTest() {
    }

    public static void run() {
        ClusterFireService service = new ClusterFireService();
        // 基础矩阵中存在两个 8 连通战船集群。
        ClusterAnalysisResult countResult = service.countClusters(SampleData.simpleFireGrid());
        TestSupport.assertEquals(2, countResult.clusterCount(), "Simple fire grid should contain 2 clusters.");

        ClusterAnalysisResult optimized = service.findOptimalIgnitionPoints(SampleData.optimizedFireGrid());
        // 每个集群都应该对应一个推荐投掷点。
        TestSupport.assertTrue(optimized.clusterCount() > 0, "Optimized ignition analysis should detect clusters.");
        TestSupport.assertEquals(
            optimized.clusterCount(),
            optimized.optimalIgnitionPoints().size(),
            "Each cluster should have one optimal ignition point."
        );
    }
}
