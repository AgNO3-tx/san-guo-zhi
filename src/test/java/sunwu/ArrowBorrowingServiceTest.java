package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.ArrowPlanResult;
import sunwu.service.ArrowBorrowingService;

import java.util.List;

public final class ArrowBorrowingServiceTest {
    private ArrowBorrowingServiceTest() {
    }

    public static void run() {
        ArrowBorrowingService service = new ArrowBorrowingService();
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

        ArrowPlanResult dynamicResult = service.planDynamicBorrowing(
            SampleData.dynamicBoatConfig(),
            List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400)
        );
        TestSupport.assertTrue(dynamicResult.totalArrows() > 0, "Dynamic borrowing should capture some arrows.");
        TestSupport.assertEquals(9, dynamicResult.directions().size(), "Dynamic result should cover each wave.");
    }
}
