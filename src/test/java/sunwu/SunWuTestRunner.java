package sunwu;

import java.util.ArrayList;
import java.util.List;

/**
 * 简易测试运行器。
 * 项目没有引入 JUnit，因此用 Runnable 列表集中运行所有测试。
 */
public final class SunWuTestRunner {
    private SunWuTestRunner() {
    }

    public static void main(String[] args) {
        List<String> failures = new ArrayList<>();
        // 每个测试类负责一个功能模块，失败会被收集后统一输出。
        run("HierarchyServiceTest", HierarchyServiceTest::run, failures);
        run("GeneralAnalyticsServiceTest", GeneralAnalyticsServiceTest::run, failures);
        run("ArrowBorrowingServiceTest", ArrowBorrowingServiceTest::run, failures);
        run("BattlefieldPathServiceTest", BattlefieldPathServiceTest::run, failures);
        run("FoodHarvestServiceTest", FoodHarvestServiceTest::run, failures);
        run("CipherServiceTest", CipherServiceTest::run, failures);
        run("ClusterFireServiceTest", ClusterFireServiceTest::run, failures);
        run("MazeEscapeServiceTest", MazeEscapeServiceTest::run, failures);
        run("DisplayTextTest", DisplayTextTest::run, failures);
        run("VisualizationTraceTest", VisualizationTraceTest::run, failures);

        if (!failures.isEmpty()) {
            System.err.println("FAILED TESTS:");
            for (String failure : failures) {
                System.err.println(failure);
            }
            System.exit(1);
        }

        System.out.println("All SunWu tests passed.");
    }

    /**
     * 执行单个测试并把异常转成失败信息，避免一个失败阻断后续测试。
     */
    private static void run(String testName, Runnable runnable, List<String> failures) {
        try {
            runnable.run();
            System.out.println("[PASS] " + testName);
        } catch (Throwable throwable) {
            failures.add("[FAIL] " + testName + ": " + throwable.getMessage());
        }
    }
}
