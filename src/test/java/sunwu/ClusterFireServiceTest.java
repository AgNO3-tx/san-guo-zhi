package sunwu;

import sunwu.data.SampleData;
import sunwu.service.ClusterAnalysisResult;
import sunwu.service.ClusterFireService;

public final class ClusterFireServiceTest {
    private ClusterFireServiceTest() {
    }

    public static void run() {
        ClusterFireService service = new ClusterFireService();
        ClusterAnalysisResult countResult = service.countClusters(SampleData.simpleFireGrid());
        TestSupport.assertEquals(2, countResult.clusterCount(), "Simple fire grid should contain 2 clusters.");

        ClusterAnalysisResult optimized = service.findOptimalIgnitionPoints(SampleData.optimizedFireGrid());
        TestSupport.assertTrue(optimized.clusterCount() > 0, "Optimized ignition analysis should detect clusters.");
        TestSupport.assertEquals(
            optimized.clusterCount(),
            optimized.optimalIgnitionPoints().size(),
            "Each cluster should have one optimal ignition point."
        );
    }
}
