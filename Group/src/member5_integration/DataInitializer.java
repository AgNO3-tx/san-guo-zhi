package member5_integration;

import member1_character.Character;

import java.util.*;

/**
 * 数据初始化工具 - 提供所有模块的默认测试数据
 */
public class DataInitializer {

    /**
     * 初始化吴国武将数据
     */
    public static List<Character> initWuCharacters() {
        List<Character> characters = new ArrayList<>();

        // 军事部武将（武力 > 智力）
        characters.add(new Character(1, "周瑜", 95, 70, 98, 85, 88));
        characters.add(new Character(2, "黄盖", 80, 92, 65, 55, 90));
        characters.add(new Character(3, "甘宁", 75, 95, 60, 40, 85));
        characters.add(new Character(4, "吕蒙", 85, 82, 88, 70, 80));
        characters.add(new Character(5, "陆逊", 88, 72, 96, 82, 78));
        characters.add(new Character(6, "程普", 78, 85, 70, 65, 82));
        characters.add(new Character(7, "韩当", 70, 80, 55, 50, 78));
        characters.add(new Character(8, "周泰", 72, 93, 45, 35, 92));
        characters.add(new Character(9, "蒋钦", 65, 78, 58, 52, 75));
        characters.add(new Character(10, "陈武", 60, 82, 48, 40, 80));

        // 内政部武将（智力 > 武力）
        characters.add(new Character(11, "张昭", 90, 35, 95, 98, 65));
        characters.add(new Character(12, "张纮", 82, 30, 92, 95, 60));
        characters.add(new Character(13, "顾雍", 85, 38, 88, 92, 62));
        characters.add(new Character(14, "诸葛瑾", 78, 42, 85, 90, 68));
        characters.add(new Character(15, "步骘", 72, 45, 80, 85, 65));
        characters.add(new Character(16, "虞翻", 68, 40, 82, 78, 58));
        characters.add(new Character(17, "薛综", 65, 35, 78, 80, 55));
        characters.add(new Character(18, "陆绩", 60, 30, 85, 75, 50));
        characters.add(new Character(19, "严峻", 62, 38, 76, 82, 58));
        characters.add(new Character(20, "程秉", 58, 32, 72, 78, 52));

        return characters;
    }

    /**
     * 初始化草船借箭数据
     */
    public static int[] initScarecrows() {
        return new int[]{80, 60, 70, 50}; // 前、左、右、后
    }

    /**
     * 初始化战船矩阵
     */
    public static int[][] initBattleGrid() {
        return new int[][]{
                {1, 1, 0, 0, 0, 1, 1},
                {1, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1, 1},
                {1, 1, 0, 0, 0, 1, 1}
        };
    }

    /**
     * 初始化迷宫
     */
    public static int[][] initMaze() {
        return new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
    }
}
