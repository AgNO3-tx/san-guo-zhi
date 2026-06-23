package sunwu.ui;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.service.ArrowBorrowingService;
import sunwu.service.BattlefieldPathService;
import sunwu.service.CipherService;
import sunwu.service.ClusterFireService;
import sunwu.service.FoodHarvestService;
import sunwu.service.GeneralAnalyticsService;
import sunwu.service.HierarchyService;
import sunwu.service.MazeEscapeService;
import sunwu.service.ReportFormatter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.Set;

/**
 * Swing 文本看板。
 * 它不重新实现算法，只调用服务层并把格式化后的文本放入标签页。
 */
public final class SunWuDashboard {
    private static final ReportFormatter FORMATTER = new ReportFormatter();

    private SunWuDashboard() {
    }

    public static void showWindow() {
        // Swing 组件必须在事件派发线程中创建。
        SwingUtilities.invokeLater(SunWuDashboard::createAndShow);
    }

    /**
     * 创建主窗口和八个功能标签页。
     */
    private static void createAndShow() {
        JFrame frame = new JFrame("Sun Wu Battle System");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1100, 780);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1 权力树", scrollable(buildHierarchyText()));
        tabs.addTab("2 武将/组队", scrollable(buildGeneralText()));
        tabs.addTab("3 草船借箭", scrollable(buildArrowText()));
        tabs.addTab("4 战场路径", scrollable(buildBattlefieldText()));
        tabs.addTab("5 粮草", scrollable(buildFoodText()));
        tabs.addTab("6 密文", scrollable(buildCipherText()));
        tabs.addTab("7 火攻矩阵", scrollable(buildFireText()));
        tabs.addTab("8 华容道", scrollable(buildMazeText()));

        frame.setLayout(new BorderLayout());
        frame.add(tabs, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 把一段文本包装成只读、可滚动的展示区域。
     */
    private static JScrollPane scrollable(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        return new JScrollPane(area);
    }

    /**
     * 构建权力树页签文本。
     */
    public static String buildHierarchyText() {
        return FORMATTER.formatHierarchy(new HierarchyService().buildHierarchy(SampleData.generals()));
    }

    /**
     * 构建武将列表和组队排行榜页签文本。
     */
    public static String buildGeneralText() {
        GeneralAnalyticsService service = new GeneralAnalyticsService();
        StringBuilder builder = new StringBuilder();
        builder.append(FORMATTER.formatRoster(SampleData.generals())).append("\n");
        builder.append("Politic team ranking\n");
        builder.append(FORMATTER.formatTeamRankings(service.rankTeamsForAbility(SampleData.generals(), AbilityType.POLITIC), 10));
        builder.append("\nStrength team ranking\n");
        builder.append(FORMATTER.formatTeamRankings(service.rankTeamsForAbility(SampleData.generals(), AbilityType.STRENGTH), 10));
        return builder.toString();
    }

    /**
     * 构建草船借箭基础和动态规则页签文本。
     */
    public static String buildArrowText() {
        ArrowBorrowingService service = new ArrowBorrowingService();
        return FORMATTER.formatArrowPlan(
            "Classic Straw Boat Borrowing",
            service.planClassicBorrowing(SampleData.classicBoatConfig(), List.of(2000, 1500, 1000, 800, 600, 500, 300, 300))
        ) + "\n" + FORMATTER.formatArrowPlan(
            "Dynamic Straw Boat Borrowing",
            service.planDynamicBorrowing(SampleData.dynamicBoatConfig(), List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400))
        );
    }

    /**
     * 构建 BFS 路径和地形最短时间页签文本。
     */
    public static String buildBattlefieldText() {
        BattlefieldPathService service = new BattlefieldPathService();
        var xuSheng = SampleData.generals().stream().filter(general -> general.name().equals("Xu Sheng")).findFirst().orElseThrow();
        return FORMATTER.formatPaths("BFS shortest paths to node 8", service.findShortestPaths(SampleData.battlefieldGraph(), 1, 8).paths())
            + "\nWeighted terrain path for Xu Sheng\n"
            + FORMATTER.formatWeightedPath(service.findShortestTimePath(SampleData.weightedBattlefieldGraph(), xuSheng, 8));
    }

    /**
     * 构建粮草三个子功能页签文本。
     */
    public static String buildFoodText() {
        FoodHarvestService service = new FoodHarvestService();
        return "Food harvesting without node 9\n"
            + FORMATTER.formatFoodSimulation(service.planFoodHarvest(SampleData.battlefieldGraph(), Set.of(9)))
            + "\nFood Harvesting I\n"
            + FORMATTER.formatFoodSimulation(service.maximizeFoodProduction(SampleData.generals(), AbilityType.POLITIC, 8))
            + "\nFood Harvesting II\n"
            + FORMATTER.formatFoodSimulation(service.planGuardedCampSimulation(SampleData.weightedBattlefieldGraph(), SampleData.generals()));
    }

    /**
     * 构建 Caesar 解密和扩展加密页签文本。
     */
    public static String buildCipherText() {
        CipherService service = new CipherService();
        String sample = "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.";
        String secure = service.encryptSecure("Attack at dawn", 3);
        return "Classic decrypt\n"
            + service.decryptClassic(sample, 7).decoded()
            + "\n\nSecure extension\nEncrypted: " + secure
            + "\nDecrypted: " + service.decryptSecure(secure)
            + "\n";
    }

    /**
     * 构建火攻矩阵和最优投掷点页签文本。
     */
    public static String buildFireText() {
        ClusterFireService service = new ClusterFireService();
        return FORMATTER.formatClusterSummary(SampleData.simpleFireGrid(), service.countClusters(SampleData.simpleFireGrid()))
            + "\n"
            + FORMATTER.formatClusterSummary(SampleData.optimizedFireGrid(), service.findOptimalIgnitionPoints(SampleData.optimizedFireGrid()));
    }

    /**
     * 构建华容道迷宫页签文本。
     */
    public static String buildMazeText() {
        MazeEscapeService service = new MazeEscapeService();
        return FORMATTER.formatMazeWithPath(SampleData.huaRongMaze(), service.escape(SampleData.huaRongMaze()));
    }
}
