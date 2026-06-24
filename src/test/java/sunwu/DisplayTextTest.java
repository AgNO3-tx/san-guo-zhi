package sunwu;

import sunwu.app.ConsoleMenu;
import sunwu.data.SampleData;
import sunwu.common.AbilityType;
import sunwu.feature.redcliff.ClusterFireService;
import sunwu.feature.soldier.GeneralAnalyticsService;
import sunwu.feature.hierarchy.HierarchyService;
import sunwu.feature.huarongroad.MazeEscapeService;
import sunwu.common.ReportFormatter;
import sunwu.feature.gui.DashboardFeature;
import sunwu.feature.gui.SunWuDashboard;

import java.util.List;
import java.util.Set;

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

        // 新版 GUI 必须按英文 PDF 的作业板块组织，方便展示 basic 与 extra features 是否完整。
        List<DashboardFeature> features = SunWuDashboard.featureCatalog();
        TestSupport.assertTrue(features.size() >= 10, "Dashboard should expose basic and extra assignment sections.");
        TestSupport.assertTrue(
            features.stream().anyMatch(feature -> feature.pdfSection().contains("Forming Wu Kingdom")),
            "Dashboard should map the hierarchy feature to the PDF section."
        );
        TestSupport.assertTrue(
            features.stream().anyMatch(feature -> feature.requirementType().equals("Extra Feature")
                && feature.pdfSection().contains("Graphic User Interface")),
            "Dashboard should explicitly list the GUI extra feature."
        );
        TestSupport.assertTrue(
            SunWuDashboard.buildOverviewText().contains("Basic Features"),
            "Dashboard overview should separate basic features."
        );

        // GUI 按钮最终调用这些 builder。这里不只检查目录存在，也检查每个汇报入口能生成可展示内容。
        assertDashboardText(SunWuDashboard.buildHierarchyText(), "Sun Quan", "Hierarchy GUI output should include the root.");
        assertDashboardText(
            SunWuDashboard.buildSoldierArrangementText(AbilityType.POLITIC, 99, 3),
            "Team ranking",
            "Soldier arrangement GUI output should include team ranking."
        );
        assertDashboardText(
            SunWuDashboard.buildClassicArrowText(List.of(2000, 1500, 1000, 800, 600, 500, 300, 300)),
            "Classic Straw Boat Borrowing",
            "Classic arrow GUI output should include the original straw boat plan."
        );
        assertDashboardText(
            SunWuDashboard.buildDynamicArrowText(List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400)),
            "Dynamic Straw Boat Borrowing",
            "Dynamic arrow GUI output should include the extra rule plan."
        );
        assertDashboardText(
            SunWuDashboard.buildFortressBfsText(8),
            "BFS shortest paths to node 8",
            "Fortress GUI output should include BFS shortest paths."
        );
        assertDashboardText(
            SunWuDashboard.buildWeightedPathText("Xu Sheng", 8),
            "Total time",
            "Weighted fortress GUI output should include Dijkstra total time."
        );
        assertDashboardText(
            SunWuDashboard.buildFoodHarvestText(Set.of(9)),
            "Food harvesting without nodes",
            "Food harvesting GUI output should show filtered route planning."
        );
        assertDashboardText(
            SunWuDashboard.buildFoodProductionText(AbilityType.POLITIC, 8),
            "Food Harvesting I",
            "Food Harvesting I GUI output should show the extra feature."
        );
        assertDashboardText(
            SunWuDashboard.buildGuardedCampText(),
            "Food Harvesting II",
            "Food Harvesting II GUI output should show guarded camp simulation."
        );
        assertDashboardText(
            SunWuDashboard.buildCipherText("^olssv$", 7),
            "Decoded",
            "Encrypted Text GUI output should include decoded text."
        );
        assertDashboardText(
            SunWuDashboard.buildSecureCipherText("Attack at dawn", 3),
            "Decrypted",
            "Secure text GUI output should verify decrypting the generated cipher."
        );
        assertDashboardText(
            SunWuDashboard.buildFireClusterText(),
            "Cluster count",
            "Red Cliff GUI output should show cluster count."
        );
        assertDashboardText(
            SunWuDashboard.buildOptimalFireText(),
            "Optimal ignition points",
            "Optimized fire GUI output should show optimized points."
        );
        assertDashboardText(
            SunWuDashboard.buildMazeText(),
            "Hua Rong Road Maze",
            "Hua Rong Road GUI output should show the maze."
        );
    }

    private static void assertDashboardText(String actual, String expected, String message) {
        TestSupport.assertTrue(actual.contains(expected), message);
    }
}
