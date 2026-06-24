package sunwu.data;

import sunwu.common.ArmyType;
import sunwu.feature.fortress.BattlefieldGraph;
import sunwu.feature.strawboats.BoatConfig;
import sunwu.feature.fortress.Edge;
import sunwu.common.General;
import sunwu.feature.fortress.TerrainType;
import sunwu.feature.fortress.WeightedBattlefieldGraph;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一的演示数据入口。
 * 所有功能都从这里取样例，便于控制台、GUI 和测试保持同一套数据。
 */
public final class SampleData {
    private SampleData() {
    }

    public static List<General> generals() {
        // PDF 中给出的吴国人物数据，包含兵种与五项能力。
        return List.of(
            new General("Zhang Zhao", ArmyType.ARCHER, 22, 80, 89, 99, 60),
            new General("Zhou Yu", ArmyType.CAVALRY, 80, 86, 97, 80, 90),
            new General("Xu Sheng", ArmyType.ARCHER, 90, 78, 72, 40, 94),
            new General("Zhu Ge Jin", ArmyType.ARCHER, 63, 61, 88, 82, 71),
            new General("Lu Su", ArmyType.INFANTRY, 43, 87, 84, 88, 53),
            new General("Tai Shi Ci", ArmyType.CAVALRY, 96, 81, 43, 33, 97),
            new General("Xiao Qiao", ArmyType.INFANTRY, 42, 52, 89, 77, 34),
            new General("Da Qiao", ArmyType.CAVALRY, 39, 62, 90, 62, 41),
            new General("Zhou Tai", ArmyType.INFANTRY, 92, 89, 72, 43, 99),
            new General("Gan Ning", ArmyType.ARCHER, 98, 92, 45, 23, 97),
            new General("Lu Meng", ArmyType.CAVALRY, 70, 77, 93, 83, 88),
            new General("Huang Gai", ArmyType.INFANTRY, 83, 98, 72, 42, 89)
        );
    }

    public static BoatConfig classicBoatConfig() {
        // 使用 LinkedHashMap 保持 front/left/right/back 的输出顺序稳定。
        Map<String, Integer> directions = new LinkedHashMap<>();
        directions.put("front", 10);
        directions.put("left", 50);
        directions.put("right", 50);
        directions.put("back", 15);
        return new BoatConfig(directions);
    }

    public static BoatConfig dynamicBoatConfig() {
        // 动态草船借箭沿用同一套草人配置，只改变箭雨和使用次数规则。
        return classicBoatConfig();
    }

    public static BattlefieldGraph battlefieldGraph() {
        // 敌营进攻和粮草基础路线共用的战场邻接表。
        return new BattlefieldGraph(Map.of(
            1, List.of(2, 3, 6, 10),
            2, List.of(1, 4),
            3, List.of(1, 4, 7),
            4, List.of(2, 3, 5),
            5, List.of(4, 6, 7),
            6, List.of(1, 5, 7, 8),
            7, List.of(5, 6, 8, 9),
            8, List.of(6, 7, 9, 10),
            9, List.of(7, 8, 10),
            10, List.of(1, 8, 9)
        ));
    }

    public static WeightedBattlefieldGraph weightedBattlefieldGraph() {
        // PDF 地形表使用 "1 - 6" 这类连线记法，因此扩展题按无向地形边展开。
        return new WeightedBattlefieldGraph(Map.of(
            1, List.of(
                new Edge(2, TerrainType.FOREST),
                new Edge(3, TerrainType.FLAT),
                new Edge(6, TerrainType.FLAT),
                new Edge(10, TerrainType.FLAT)
            ),
            2, List.of(
                new Edge(1, TerrainType.FOREST),
                new Edge(4, TerrainType.SWAMP)
            ),
            3, List.of(
                new Edge(1, TerrainType.FLAT),
                new Edge(4, TerrainType.SWAMP),
                new Edge(7, TerrainType.PLANK)
            ),
            4, List.of(
                new Edge(2, TerrainType.SWAMP),
                new Edge(3, TerrainType.SWAMP),
                new Edge(5, TerrainType.SWAMP)
            ),
            5, List.of(
                new Edge(4, TerrainType.SWAMP),
                new Edge(6, TerrainType.FLAT),
                new Edge(7, TerrainType.FOREST)
            ),
            6, List.of(
                new Edge(1, TerrainType.FLAT),
                new Edge(5, TerrainType.FLAT),
                new Edge(7, TerrainType.FOREST),
                new Edge(8, TerrainType.PLANK)
            ),
            7, List.of(
                new Edge(3, TerrainType.PLANK),
                new Edge(5, TerrainType.FOREST),
                new Edge(6, TerrainType.FOREST),
                new Edge(8, TerrainType.FLAT),
                new Edge(9, TerrainType.FLAT)
            ),
            8, List.of(
                new Edge(6, TerrainType.PLANK),
                new Edge(7, TerrainType.FLAT),
                new Edge(9, TerrainType.SWAMP),
                new Edge(10, TerrainType.FOREST)
            ),
            9, List.of(
                new Edge(7, TerrainType.FLAT),
                new Edge(8, TerrainType.SWAMP),
                new Edge(10, TerrainType.FLAT)
            ),
            10, List.of(
                new Edge(1, TerrainType.FLAT),
                new Edge(8, TerrainType.FOREST),
                new Edge(9, TerrainType.FLAT)
            )
        ));
    }

    public static int[][] simpleFireGrid() {
        // 火烧连环船基础题矩阵：1 表示战船，0 表示空位。
        return new int[][]{
            {1, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 1, 1},
            {0, 0, 0, 1}
        };
    }

    public static int[][] optimizedFireGrid() {
        // 火攻最优投掷点扩展题矩阵，用于分析集群内部最佳坐标。
        return new int[][]{
            {1, 1, 0, 0, 0},
            {1, 1, 1, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 1, 1, 0, 0},
            {1, 0, 0, 0, 0}
        };
    }

    public static int[][] huaRongMaze() {
        // 华容道迷宫：1=墙，0=通路，2=起点，3=出口。
        return new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
    }
}
