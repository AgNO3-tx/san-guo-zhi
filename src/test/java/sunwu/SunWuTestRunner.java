package sunwu;

import java.util.ArrayList;
import java.util.List;

public final class SunWuTestRunner {
    private SunWuTestRunner() {
    }

    public static void main(String[] args) {
        List<String> failures = new ArrayList<>();
        run("HierarchyServiceTest", HierarchyServiceTest::run, failures);
        run("GeneralAnalyticsServiceTest", GeneralAnalyticsServiceTest::run, failures);
        run("ArrowBorrowingServiceTest", ArrowBorrowingServiceTest::run, failures);
        run("BattlefieldPathServiceTest", BattlefieldPathServiceTest::run, failures);
        run("FoodHarvestServiceTest", FoodHarvestServiceTest::run, failures);
        run("CipherServiceTest", CipherServiceTest::run, failures);
        run("ClusterFireServiceTest", ClusterFireServiceTest::run, failures);
        run("MazeEscapeServiceTest", MazeEscapeServiceTest::run, failures);
        run("DisplayTextTest", DisplayTextTest::run, failures);

        if (!failures.isEmpty()) {
            System.err.println("FAILED TESTS:");
            for (String failure : failures) {
                System.err.println(failure);
            }
            System.exit(1);
        }

        System.out.println("All SunWu tests passed.");
    }

    private static void run(String testName, Runnable runnable, List<String> failures) {
        try {
            runnable.run();
            System.out.println("[PASS] " + testName);
        } catch (Throwable throwable) {
            failures.add("[FAIL] " + testName + ": " + throwable.getMessage());
        }
    }
}
