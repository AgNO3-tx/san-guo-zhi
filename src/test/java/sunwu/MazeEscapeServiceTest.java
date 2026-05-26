package sunwu;

import sunwu.data.SampleData;
import sunwu.service.MazeEscapeResult;
import sunwu.service.MazeEscapeService;

public final class MazeEscapeServiceTest {
    private MazeEscapeServiceTest() {
    }

    public static void run() {
        MazeEscapeService service = new MazeEscapeService();
        MazeEscapeResult result = service.escape(SampleData.huaRongMaze());

        TestSupport.assertTrue(!result.path().isEmpty(), "Maze path should not be empty.");
        TestSupport.assertEquals(result.start(), result.path().getFirst(), "Path should start at the maze entry.");
        TestSupport.assertEquals(result.exit(), result.path().getLast(), "Path should end at the maze exit.");
    }
}
