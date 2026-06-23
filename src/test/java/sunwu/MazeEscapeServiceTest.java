package sunwu;

import sunwu.data.SampleData;
import sunwu.service.MazeEscapeResult;
import sunwu.service.MazeEscapeService;

/**
 * 验证华容道迷宫 BFS 能找到从入口到出口的路径。
 */
public final class MazeEscapeServiceTest {
    private MazeEscapeServiceTest() {
    }

    public static void run() {
        MazeEscapeService service = new MazeEscapeService();
        MazeEscapeResult result = service.escape(SampleData.huaRongMaze());

        // 路径不要求逐点固定，但必须从起点出发并到达出口。
        TestSupport.assertTrue(!result.path().isEmpty(), "Maze path should not be empty.");
        TestSupport.assertEquals(result.start(), result.path().getFirst(), "Path should start at the maze entry.");
        TestSupport.assertEquals(result.exit(), result.path().getLast(), "Path should end at the maze exit.");
    }
}
