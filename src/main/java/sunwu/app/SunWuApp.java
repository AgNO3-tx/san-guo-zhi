package sunwu.app;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.ArrowPlanResult;
import sunwu.domain.General;
import sunwu.service.ArrowBorrowingService;
import sunwu.service.BattlefieldPathService;
import sunwu.service.CipherService;
import sunwu.service.ClusterFireService;
import sunwu.service.FoodHarvestService;
import sunwu.service.GeneralAnalyticsService;
import sunwu.service.HierarchyService;
import sunwu.service.MazeEscapeService;
import sunwu.service.ReportFormatter;
import sunwu.ui.SunWuDashboard;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统的控制台主入口。
 * 这个类只负责菜单调度、读取用户输入和调用服务层，不直接实现具体算法。
 */
public final class SunWuApp {
    // 原题草船借箭样例：箭雨数量按轮次递减。
    private static final List<Integer> CLASSIC_ARROWS = List.of(2000, 1500, 1000, 800, 600, 500, 300, 300);
    // 扩展题样例：箭雨数量不再有序，用来测试动态策略。
    private static final List<Integer> DYNAMIC_ARROWS = List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400);
    // PDF 中 Caesar 密文样例，默认用于演示特殊语法解密。
    private static final String SAMPLE_CIPHER = "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.";

    private final Scanner scanner;
    private final ReportFormatter formatter = new ReportFormatter();
    private final HierarchyService hierarchyService = new HierarchyService();
    private final GeneralAnalyticsService analyticsService = new GeneralAnalyticsService();
    private final ArrowBorrowingService arrowBorrowingService = new ArrowBorrowingService();
    private final BattlefieldPathService battlefieldPathService = new BattlefieldPathService();
    private final FoodHarvestService foodHarvestService = new FoodHarvestService();
    private final CipherService cipherService = new CipherService();
    private final ClusterFireService clusterFireService = new ClusterFireService();
    private final MazeEscapeService mazeEscapeService = new MazeEscapeService();

    private SunWuApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        new SunWuApp(new Scanner(System.in)).run();
    }

    /**
     * 主循环：显示菜单、读取编号、分派到对应演示方法。
     */
    private void run() {
        while (true) {
            System.out.println(ConsoleMenu.mainMenu());
            String choice = readLine("请选择功能编号: ");
            switch (choice) {
                case "1" -> showHierarchy();
                case "2" -> sortAndSearchGenerals();
                case "3" -> buildTeamByAbility();
                case "4" -> showClassicArrowBorrowing();
                case "5" -> showDynamicArrowBorrowing();
                case "6" -> showFortressBfs();
                case "7" -> showFoodHarvest();
                case "8" -> showFoodProduction();
                case "9" -> showGuardedCampSimulation();
                case "10" -> showClassicCipher();
                case "11" -> showSecureCipher();
                case "12" -> showFireClusters();
                case "13" -> showOptimalFirePoints();
                case "14" -> showMazeEscape();
                case "15" -> showWeightedFortressPath();
                case "16" -> SunWuDashboard.showWindow();
                case "17" -> showAllSampleData();
                case "0" -> {
                    return;
                }
                default -> System.out.println("无效选项，请重新输入。");
            }
            System.out.println();
        }
    }

    // 以下 showXxx 方法是菜单到服务层之间的薄封装，便于每个功能独立演示。
    private void showHierarchy() {
        System.out.println(formatter.formatHierarchy(hierarchyService.buildHierarchy(SampleData.generals())));
    }

    private void sortAndSearchGenerals() {
        AbilityType ability = askAbility(AbilityType.STRENGTH);
        System.out.println(formatter.formatSortedGenerals(SampleData.generals(), ability));
        int target = askInt("输入要二分查找的能力值，回车使用该属性最高值: ",
            SampleData.generals().stream().mapToInt(general -> general.ability(ability)).max().orElse(0));
        List<String> matches = analyticsService.searchByAbility(SampleData.generals(), ability, target);
        if (matches.isEmpty()) {
            System.out.println("Binary search result: no general with " + formatter.label(ability) + " = " + target);
        } else {
            System.out.println("Binary search result: " + matches);
        }
    }

    private void buildTeamByAbility() {
        AbilityType ability = askAbility(AbilityType.POLITIC);
        var rankings = analyticsService.rankTeamsForAbility(SampleData.generals(), ability);
        System.out.println(formatter.formatTeamSuggestion(rankings.getFirst()));
        int limit = askInt("显示前几组排行？回车显示前10组: ", 10);
        System.out.println(formatter.formatTeamRankings(rankings, limit));
    }

    private void showClassicArrowBorrowing() {
        List<Integer> waves = askIntegerList("输入箭雨数组，逗号分隔，回车使用原文样例: ", CLASSIC_ARROWS);
        ArrowPlanResult result = arrowBorrowingService.planClassicBorrowing(SampleData.classicBoatConfig(), waves);
        System.out.println(formatter.formatArrowPlan("Classic Straw Boat Borrowing", result));
    }

    private void showDynamicArrowBorrowing() {
        List<Integer> waves = askIntegerList("输入随机箭雨数组，逗号分隔，回车使用扩展样例: ", DYNAMIC_ARROWS);
        ArrowPlanResult result = arrowBorrowingService.planDynamicBorrowing(SampleData.dynamicBoatConfig(), waves);
        System.out.println(formatter.formatArrowPlan("Dynamic Straw Boat Borrowing", result));
    }

    private void showFortressBfs() {
        int target = askInt("输入敌军大本营节点，回车使用 8: ", 8);
        var result = battlefieldPathService.findShortestPaths(SampleData.battlefieldGraph(), 1, target);
        System.out.println(formatter.formatPaths("BFS shortest paths from 1 to " + target, result.paths()));
    }

    private void showFoodHarvest() {
        Set<Integer> noFoodNodes = new LinkedHashSet<>(askIntegerList("输入无粮节点，逗号分隔，回车使用 9: ", List.of(9)));
        var result = foodHarvestService.planFoodHarvest(SampleData.battlefieldGraph(), noFoodNodes);
        System.out.println("Nodes without food: " + noFoodNodes);
        System.out.println(formatter.formatFoodSimulation(result));
    }

    private void showFoodProduction() {
        AbilityType ability = askPoliticOrIntelligence();
        int nodeCount = askInt("输入有粮节点数量，回车使用 8: ", 8);
        var result = foodHarvestService.maximizeFoodProduction(SampleData.generals(), ability, nodeCount);
        System.out.println(formatter.formatFoodSimulation(result));
    }

    private void showGuardedCampSimulation() {
        var result = foodHarvestService.planGuardedCampSimulation(SampleData.weightedBattlefieldGraph(), SampleData.generals());
        System.out.println(formatter.formatFoodSimulation(result));
    }

    private void showClassicCipher() {
        String text = readLine("输入密文，回车使用原文样例: ");
        if (text.isBlank()) {
            text = SAMPLE_CIPHER;
        }
        int shift = askInt("输入 Caesar shift，回车使用 7: ", 7);
        System.out.println(cipherService.decryptClassic(text, shift).decoded());
    }

    private void showSecureCipher() {
        String plainText = readLine("输入明文，回车使用 Attack at dawn: ");
        if (plainText.isBlank()) {
            plainText = "Attack at dawn";
        }
        int rule = askInt("输入 &num{} 规则数字，回车使用 3: ", 3);
        String encrypted = cipherService.encryptSecure(plainText, rule);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + cipherService.decryptSecure(encrypted));
    }

    private void showFireClusters() {
        int[][] grid = SampleData.simpleFireGrid();
        System.out.println(formatter.formatClusterSummary(grid, clusterFireService.countClusters(grid)));
    }

    private void showOptimalFirePoints() {
        int[][] grid = SampleData.optimizedFireGrid();
        System.out.println(formatter.formatClusterSummary(grid, clusterFireService.findOptimalIgnitionPoints(grid)));
    }

    private void showMazeEscape() {
        int[][] maze = SampleData.huaRongMaze();
        System.out.println(formatter.formatMazeWithPath(maze, mazeEscapeService.escape(maze)));
    }

    private void showWeightedFortressPath() {
        General general = askGeneral("输入武将姓名，回车使用 Xu Sheng: ", "Xu Sheng");
        int target = askInt("输入敌军大本营节点，回车使用 8: ", 8);
        System.out.println(general.name() + " [" + general.armyType() + "]");
        System.out.println(formatter.formatWeightedPath(
            battlefieldPathService.findShortestTimePath(SampleData.weightedBattlefieldGraph(), general, target)
        ));
    }

    private void showAllSampleData() {
        System.out.println(formatter.formatRoster(SampleData.generals()));
        System.out.println(formatter.formatGrid("Fire Matrix", SampleData.simpleFireGrid()));
        System.out.println(formatter.formatGrid("Hua Rong Road Maze", SampleData.huaRongMaze()));
        System.out.println("Battlefield graph: " + SampleData.battlefieldGraph().adjacency());
    }

    private AbilityType askAbility(AbilityType defaultValue) {
        System.out.print(ConsoleMenu.abilityPrompt());
        String input = scanner.nextLine().trim();
        // 输入非法或直接回车时使用调用方给定的默认属性。
        return switch (input) {
            case "1" -> AbilityType.LEADERSHIP;
            case "2" -> AbilityType.STRENGTH;
            case "3" -> AbilityType.INTELLIGENCE;
            case "4" -> AbilityType.POLITIC;
            case "5" -> AbilityType.HIT_POINT;
            default -> defaultValue;
        };
    }

    private AbilityType askPoliticOrIntelligence() {
        String input = readLine("选择产量队伍属性：1=Politic，2=Intelligence，回车使用 Politic: ");
        return "2".equals(input) ? AbilityType.INTELLIGENCE : AbilityType.POLITIC;
    }

    private General askGeneral(String prompt, String defaultName) {
        String name = readLine(prompt);
        if (name.isBlank()) {
            name = defaultName;
        }
        String finalName = name;
        // 武将姓名允许忽略大小写；找不到时回退到默认武将，保证演示流程不中断。
        return SampleData.generals().stream()
            .filter(general -> general.name().equalsIgnoreCase(finalName))
            .findFirst()
            .orElseGet(() -> SampleData.generals().stream()
                .filter(general -> general.name().equals(defaultName))
                .findFirst()
                .orElseThrow());
    }

    private int askInt(String prompt, int defaultValue) {
        String input = readLine(prompt);
        if (input.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            // 交互式演示中输入错误很常见，直接回退默认值比抛异常更友好。
            System.out.println("输入不是整数，使用默认值 " + defaultValue + "。");
            return defaultValue;
        }
    }

    private List<Integer> askIntegerList(String prompt, List<Integer> defaultValue) {
        String input = readLine(prompt);
        if (input.isBlank()) {
            return defaultValue;
        }
        try {
            // 支持 “1,2,3” 这样的简单逗号分隔格式。
            return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        } catch (NumberFormatException ignored) {
            System.out.println("数组格式无法解析，使用默认样例。");
            return defaultValue;
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
