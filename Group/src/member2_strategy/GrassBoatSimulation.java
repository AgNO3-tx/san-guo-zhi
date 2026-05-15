package member2_strategy;

import java.util.*;

/**
 * 草船借箭模拟
 * 船四个方向：前/左/右/后，草人数量不同
 * 敌军N轮射箭，逐轮减少
 * 每轮效率下降，单方向最多用3次
 * 计算公式：箭数 × (剩余草人 / 100)
 */
public class GrassBoatSimulation {

    private String[] directions = {"前", "左", "右", "后"};
    private int[] scarecrowCount; // 各方向草人数量
    private int[] usageCount;     // 各方向已使用次数
    private int totalArrows;      // 总获得箭数

    public GrassBoatSimulation() {
        scarecrowCount = new int[4];
        usageCount = new int[4];
        totalArrows = 0;
    }

    /**
     * 运行草船借箭模拟
     * @param scarecrows 四个方向的草人数量 [前, 左, 右, 后]
     * @param rounds 敌军射箭轮数
     */
    public void simulate(int[] scarecrows, int rounds) {
        this.scarecrowCount = scarecrows.clone();
        this.usageCount = new int[4];
        this.totalArrows = 0;

        System.out.println("\n🏹 草船借箭模拟");
        System.out.println("=".repeat(60));
        System.out.println("初始草人分布：前=" + scarecrows[0] + " 左=" + scarecrows[1]
                + " 右=" + scarecrows[2] + " 后=" + scarecrows[3]);
        System.out.println("敌军射箭轮数：" + rounds);
        System.out.println("=".repeat(60));

        Random rand = new Random();

        for (int round = 1; round <= rounds; round++) {
            // 每轮箭数逐轮减少（基础100，每轮减5）
            int baseArrows = Math.max(20, 100 - (round - 1) * 5);
            // 加入随机波动
            int arrowsThisRound = baseArrows + rand.nextInt(21) - 10;

            System.out.printf("\n第%d轮 - 敌军射来 %d 支箭\n", round, arrowsThisRound);

            // 找出最优朝向
            int bestDir = findBestDirection();
            if (bestDir == -1) {
                System.out.println("⚠ 所有方向已用完，无法继续！");
                break;
            }

            // 效率衰减：每使用一次该方向，效率降低20%
            double efficiency = Math.max(0.2, 1.0 - (usageCount[bestDir] - 1) * 0.2);
            // 计算公式：箭数 × (剩余草人 / 100) × 效率
            int gainedArrows = (int) (arrowsThisRound * (scarecrowCount[bestDir] / 100.0) * efficiency);

            // 更新数据
            scarecrowCount[bestDir] = Math.max(0, scarecrowCount[bestDir] - gainedArrows / 10);
            totalArrows += gainedArrows;

            System.out.printf("  最优朝向：%s（剩余草人%d，已用%d次）\n",
                    directions[bestDir], scarecrowCount[bestDir], usageCount[bestDir]);
            System.out.printf("  效率：%.0f%%\n", efficiency * 100);
            System.out.printf("  获得箭数：%d\n", gainedArrows);
            System.out.printf("  累计箭数：%d\n", totalArrows);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.printf("🏁 模拟结束！共获得 %d 支箭！\n", totalArrows);
        System.out.println("=".repeat(60));
    }

    /**
     * 寻找最优方向（剩余草人最多且未用完的方向）
     */
    private int findBestDirection() {
        int bestIdx = -1;
        int maxScarecrows = -1;
        for (int i = 0; i < 4; i++) {
            if (usageCount[i] < 3 && scarecrowCount[i] > 0) {
                if (scarecrowCount[i] > maxScarecrows) {
                    maxScarecrows = scarecrowCount[i];
                    bestIdx = i;
                }
            }
        }
        if (bestIdx != -1) {
            usageCount[bestIdx]++;
        }
        return bestIdx;
    }

    /**
     * 动态草船借箭（拓展版）
     * 箭数随机，草人最多用2次
     */
    public void dynamicSimulate(int[] scarecrows, int rounds) {
        this.scarecrowCount = scarecrows.clone();
        this.usageCount = new int[4];
        this.totalArrows = 0;
        Random rand = new Random();

        System.out.println("\n🏹 动态草船借箭（拓展版）");
        System.out.println("=".repeat(60));
        System.out.println("初始草人分布：前=" + scarecrows[0] + " 左=" + scarecrows[1]
                + " 右=" + scarecrows[2] + " 后=" + scarecrows[3]);
        System.out.println("规则：箭数随机，每个方向最多用2次");
        System.out.println("=".repeat(60));

        for (int round = 1; round <= rounds; round++) {
            // 箭数完全随机 20~150
            int arrowsThisRound = 20 + rand.nextInt(131);
            System.out.printf("\n第%d轮 - 敌军射来 %d 支箭\n", round, arrowsThisRound);

            int bestDir = findBestDirectionDynamic();
            if (bestDir == -1) {
                System.out.println("⚠ 所有方向已用完，无法继续！");
                break;
            }

            double efficiency = Math.max(0.3, 1.0 - (usageCount[bestDir] - 1) * 0.35);
            int gainedArrows = (int) (arrowsThisRound * (scarecrowCount[bestDir] / 100.0) * efficiency);

            scarecrowCount[bestDir] = Math.max(0, scarecrowCount[bestDir] - gainedArrows / 8);
            totalArrows += gainedArrows;

            System.out.printf("  最优朝向：%s（剩余草人%d，已用%d次）\n",
                    directions[bestDir], scarecrowCount[bestDir], usageCount[bestDir]);
            System.out.printf("  效率：%.0f%%\n", efficiency * 100);
            System.out.printf("  获得箭数：%d\n", gainedArrows);
            System.out.printf("  累计箭数：%d\n", totalArrows);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.printf("🏁 动态模拟结束！共获得 %d 支箭！\n", totalArrows);
        System.out.println("=".repeat(60));
    }

    /**
     * 动态版寻找最优方向（最多用2次）
     */
    private int findBestDirectionDynamic() {
        int bestIdx = -1;
        int maxScarecrows = -1;
        for (int i = 0; i < 4; i++) {
            if (usageCount[i] < 2 && scarecrowCount[i] > 0) {
                if (scarecrowCount[i] > maxScarecrows) {
                    maxScarecrows = scarecrowCount[i];
                    bestIdx = i;
                }
            }
        }
        if (bestIdx != -1) {
            usageCount[bestIdx]++;
        }
        return bestIdx;
    }
}
