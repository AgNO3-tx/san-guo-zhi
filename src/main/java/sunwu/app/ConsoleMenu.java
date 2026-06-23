package sunwu.app;

/**
 * 控制台菜单文本集中放在这里，避免主程序里散落大量展示字符串。
 */
public final class ConsoleMenu {
    private ConsoleMenu() {
    }

    /**
     * 返回主菜单。菜单编号必须和 {@link SunWuApp} 中的 switch 分支保持一致。
     */
    public static String mainMenu() {
        return """
            === Sun Wu Battle System ===
            1. 权力层级树
            2. 武将能力排序与二分查找
            3. 按属性组队与SABC评级
            4. 草船借箭：原题规则
            5. 草船借箭：动态扩展规则
            6. 攻打敌军堡垒：BFS最短路径
            7. 粮草采集：避开无粮节点
            8. 粮草采集I：政治/智力产量最大化
            9. 粮草采集II：三将占营模拟
            10. 密文解密：Caesar与特殊语法
            11. 更安全文本加密：扩展规则
            12. 火烧连环船：01矩阵集群统计
            13. 火攻最优投掷点：扩展规则
            14. 华容道追击：迷宫逃跑路
            15. 敌军堡垒进阶：地形最短时间
            16. 打开Swing文本看板
            17. 展示全部样例数据
            0. 退出
            """;
    }

    /**
     * 返回武将能力属性选择提示，供排序、查找、组队等功能复用。
     */
    public static String abilityPrompt() {
        return """
            选择属性:
            1. Leadership
            2. Strength
            3. Intelligence
            4. Politic
            5. Hit Point
            输入编号:
            """;
    }
}
