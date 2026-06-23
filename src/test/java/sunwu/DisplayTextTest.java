package sunwu;

import sunwu.app.ConsoleMenu;
import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.service.ClusterFireService;
import sunwu.service.GeneralAnalyticsService;
import sunwu.service.HierarchyService;
import sunwu.service.MazeEscapeService;
import sunwu.service.ReportFormatter;

/**
 * 验证菜单和格式化输出中用于汇报展示的关键文本。
 */
public final class DisplayTextTest {
    private DisplayTextTest() {
    }

    public static void run() {
        // 菜单文本是汇报入口，关键功能必须独立暴露。
        String menu = ConsoleMenu.mainMenu();
        TestSupport.assertTrue(menu.contains("1. 权力层级树"), "Menu should expose the hierarchy as its own feature.");
        TestSupport.assertTrue(menu.contains("3. 按属性组队与SABC评级"), "Menu should expose team building as its own feature.");
        TestSupport.assertTrue(menu.contains("12. 火烧连环船：01矩阵集群统计"), "Menu should expose fire attack with a 0/1 matrix.");
        TestSupport.assertTrue(!menu.contains("基础功能演示"), "Menu should not hide work under basic demo buckets.");

        // 以下断言锁定展示格式，防止后续改动把报告输出变得不可读。
        ReportFormatter formatter = new ReportFormatter();
        String hierarchy = formatter.formatHierarchy(new HierarchyService().buildHierarchy(SampleData.generals()));
        TestSupport.assertTrue(hierarchy.contains("Sun Quan"), "Hierarchy report should include the emperor.");
        TestSupport.assertTrue(hierarchy.contains("|-- Zhou Yu"), "Hierarchy report should look like an expanded tree.");
        TestSupport.assertTrue(hierarchy.contains("`--"), "Hierarchy report should include tree branch markers.");

        String ranking = formatter.formatTeamRankings(
            new GeneralAnalyticsService().rankTeamsForAbility(SampleData.generals(), AbilityType.POLITIC),
            5
        );
        TestSupport.assertTrue(ranking.contains("Rank"), "Team ranking should include ranking headers.");
        TestSupport.assertTrue(ranking.contains("Grade"), "Team ranking should include SABC grade.");
        TestSupport.assertTrue(ranking.contains("Zhang Zhao"), "Team ranking should list member names.");

        String grid = formatter.formatGrid("Fire Matrix", SampleData.simpleFireGrid());
        TestSupport.assertTrue(grid.contains("1 1 0 0"), "Grid report should display 0/1 rows.");

        String maze = formatter.formatMazeWithPath(
            SampleData.huaRongMaze(),
            new MazeEscapeService().escape(SampleData.huaRongMaze())
        );
        TestSupport.assertTrue(maze.contains("*"), "Maze report should mark the escape path.");

        String fire = formatter.formatClusterSummary(
            SampleData.simpleFireGrid(),
            new ClusterFireService().countClusters(SampleData.simpleFireGrid())
        );
        TestSupport.assertTrue(fire.contains("Cluster count: 2"), "Fire report should state cluster count.");
    }
}
