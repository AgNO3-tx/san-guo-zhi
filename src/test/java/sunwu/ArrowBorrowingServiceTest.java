package sunwu;

import sunwu.data.SampleData;
import sunwu.feature.strawboats.ArrowPlanResult;
import sunwu.feature.strawboats.ArrowBorrowingService;

import java.util.List;

/**
 * 验证草船借箭基础规则和动态规则的关键结果。
 */
public final class ArrowBorrowingServiceTest {
    private ArrowBorrowingServiceTest() {
    }

    public static void run() {
        ArrowBorrowingService service = new ArrowBorrowingService();
        // 原题样例应选择能最大化总箭数的方向序列。
        ArrowPlanResult result = service.planClassicBorrowing(
            SampleData.classicBoatConfig(),
            List.of(2000, 1500, 1000, 800, 600, 500, 300, 300)
        );

        TestSupport.assertListEquals(
            List.of("left", "right", "left", "right", "left", "right", "back", "back"),
            result.directions(),
            "Classic borrowing directions should maximize captured arrows under the document rules."
        );
        TestSupport.assertEquals(2771, result.totalArrows(), "Total arrows should match sample.");

        // 动态规则重点验证每一轮都有决策，并且能获得正数箭量。
        ArrowPlanResult dynamicResult = service.planDynamicBorrowing(
            SampleData.dynamicBoatConfig(),
            List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400)
        );
        TestSupport.assertTrue(dynamicResult.totalArrows() > 0, "Dynamic borrowing should capture some arrows.");
        TestSupport.assertEquals(9, dynamicResult.directions().size(), "Dynamic result should cover each wave.");
    }
}
