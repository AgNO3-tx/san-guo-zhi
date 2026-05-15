import member1_character.*;
import member2_strategy.*;
import member3_graph.*;
import member4_battlefield.*;
import member5_integration.*;

import java.util.*;

/**
 * 三国·赤壁之战 - 综合系统主入口
 * 整合所有组员模块，提供控制台菜单和GUI两种模式
 *
 * 组员分工：
 *   组员1 - member1_character：武将体系（树结构、排序、查找、组队）
 *   组员2 - member2_strategy：计谋与密码（草船借箭、凯撒密码、维吉尼亚密码）
 *   组员3 - member3_graph：图路径与粮草（BFS进攻路径、粮草征收、地形路径）
 *   组员4 - member4_battlefield：战场与迷宫（火烧赤壁、华容道迷宫、A*算法）
 *   组员5 - member5_integration：整合、GUI、数据初始化
 */
public class Main {
    private static List<member1_character.Character> characters;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        characters = DataInitializer.initWuCharacters();

        System.out.println("=".repeat(60));
        System.out.println("      🌊 三国·赤壁之战 - 综合系统 🌊");
        System.out.println("=".repeat(60));
        System.out.println("组员分工：");
        System.out.println("  组员1 - 武将体系（树结构、排序、查找、组队）");
        System.out.println("  组员2 - 计谋与密码（草船借箭、凯撒密码、维吉尼亚密码）");
        System.out.println("  组员3 - 图路径与粮草（BFS进攻路径、粮草征收、地形路径）");
        System.out.println("  组员4 - 战场与迷宫（火烧赤壁、华容道迷宫、A*算法）");
        System.out.println("  组员5 - 整合、GUI、数据初始化");
        System.out.println("=".repeat(60));

        while (true) {
            printMenu();
            System.out.print("请选择功能 (0-13): ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ 请输入有效数字！");
                continue;
            }

            switch (choice) {
                case 0 -> { System.out.println("👋 再见！"); return; }
                case 1 -> demoTreeStructure();
                case 2 -> demoSorting();
                case 3 -> demoBinarySearch();
                case 4 -> demoTeamRecommend();
                case 5 -> demoGrassBoat();
                case 6 -> demoCaesarCipher();
                case 7 -> demoBFSPath();
                case 8 -> demoGrainCollect();
                case 9 -> demoFireAttack();
                case 10 -> demoHuarongMaze();
                case 11 -> demoExtensions();
                case 12 -> launchGUI();
                case 13 -> demoAll();
                default -> System.out.println("⚠ 无效选项，请重新选择！");
            }

            System.out.println("\n按回车键继续...");
            try {
                scanner.nextLine();
            } catch (Exception e) {
                // 非交互模式下忽略
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 功能菜单");
        System.out.println("=".repeat(60));
        System.out.println("【武将体系】");
        System.out.println("  1. 吴国三级树形组织结构");
        System.out.println("  2. 武将多属性排序");
        System.out.println("  3. 二分查找武将");
        System.out.println("  4. S/A/B/C分级组队推荐");
        System.out.println("【计谋与密码】");
        System.out.println("  5. 草船借箭模拟");
        System.out.println("  6. 凯撒密码解密");
        System.out.println("【图路径与粮草】");
        System.out.println("  7. BFS敌营进攻路径搜索");
        System.out.println("  8. 粮草征收路径规划");
        System.out.println("【战场与迷宫】");
        System.out.println("  9. 火烧赤壁 - 战船集群统计");
        System.out.println(" 10. 华容道迷宫追击");
        System.out.println("【拓展与整合】");
        System.out.println(" 11. 拓展功能展示（维吉尼亚密码、地形路径、动态草船）");
        System.out.println(" 12. 🖥️ 启动GUI图形界面");
        System.out.println(" 13. 全部功能一键展示");
        System.out.println("  0. 退出系统");
        System.out.println("=".repeat(60));
    }

    // ===== 组员1功能 =====
    private static void demoTreeStructure() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员1】吴国三级树形组织结构");
        System.out.println("=".repeat(60));
        TreeStructure tree = new TreeStructure();
        tree.buildTree(characters);
        tree.printHierarchy();
        tree.printDepartmentStats();
    }

    private static void demoSorting() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员1】武将多属性排序");
        System.out.println("=".repeat(60));
        CharacterSorter sorter = new CharacterSorter();
        sorter.printAllSorts(characters);
    }

    private static void demoBinarySearch() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员1】二分查找武将");
        System.out.println("=".repeat(60));
        BinarySearch bs = new BinarySearch();
        bs.demoSearch(characters);
    }

    private static void demoTeamRecommend() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员1】S/A/B/C分级组队推荐");
        System.out.println("=".repeat(60));
        TeamRecommender recommender = new TeamRecommender();
        recommender.printGrades(characters);
        recommender.printTeamRecommendations(characters);
    }

    // ===== 组员2功能 =====
    private static void demoGrassBoat() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员2】草船借箭模拟");
        System.out.println("=".repeat(60));
        GrassBoatSimulation boat = new GrassBoatSimulation();
        boat.simulate(DataInitializer.initScarecrows(), 8);
    }

    private static void demoCaesarCipher() {
        CaesarCipher cc = new CaesarCipher();
        cc.runExamples();
        System.out.println("\n是否自定义解密？(y/n): ");
        String choice = scanner.nextLine().trim();
        if (choice.equalsIgnoreCase("y")) {
            cc.demoDecrypt();
        }
    }

    // ===== 组员3功能 =====
    private static void demoBFSPath() {
        BFSPathFinder finder = new BFSPathFinder(Graph.buildDefaultMap());
        finder.runDefault();
    }

    private static void demoGrainCollect() {
        GrainCollector collector = new GrainCollector(
                Graph.buildGrainMap(), Graph.getDefaultGrainMap());
        collector.runDefault();
    }

    // ===== 组员4功能 =====
    private static void demoFireAttack() {
        FireAttack fire = new FireAttack();
        fire.printAnalysis(DataInitializer.initBattleGrid());
        fire.findOptimalFirePoint(DataInitializer.initBattleGrid());
    }

    private static void demoHuarongMaze() {
        HuarongMaze maze = new HuarongMaze();
        maze.printPath(DataInitializer.initMaze());
        maze.printPathAStar(DataInitializer.initMaze());
    }

    // ===== 组员5功能 =====
    private static void demoExtensions() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("【组员5】拓展功能展示");
        System.out.println("=".repeat(60));

        // 拓展1：维吉尼亚密码
        VigenereCipher vc = new VigenereCipher();
        vc.runExamples();
        vc.securityAnalysis();

        // 拓展2：地形路径
        TerrainPathFinder finder = TerrainPathFinder.buildDefaultMap();
        finder.runDefault();

        // 拓展3：动态草船借箭
        GrassBoatSimulation boat = new GrassBoatSimulation();
        boat.dynamicSimulate(DataInitializer.initScarecrows(), 8);
    }

    private static void launchGUI() {
        System.out.println("\n🖥️ 启动GUI图形界面...");
        GUIApplication app = new GUIApplication();
        app.launch();
        System.out.println("✅ GUI已启动，请在窗口中操作");
    }

    private static void demoAll() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("      🌊 全部功能一键展示 🌊");
        System.out.println("=".repeat(60));

        demoTreeStructure();
        pause(500);
        demoSorting();
        pause(500);
        demoTeamRecommend();
        pause(500);
        demoGrassBoat();
        pause(500);
        demoCaesarCipher();
        pause(500);
        demoBFSPath();
        pause(500);
        demoGrainCollect();
        pause(500);
        demoFireAttack();
        pause(500);
        demoHuarongMaze();
        pause(500);
        demoExtensions();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ 全部功能展示完成！");
        System.out.println("=".repeat(60));
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
