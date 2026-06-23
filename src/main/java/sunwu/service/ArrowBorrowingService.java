package sunwu.service;

import sunwu.domain.ArrowPlanResult;
import sunwu.domain.BoatConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 草船借箭策略服务。
 * 通过贪心方式逐轮选择预计获得箭数最多的方向。
 */
public final class ArrowBorrowingService {
    /**
     * 原题规则：每个方向最多使用三次，效率依次为 100%、80%、40%。
     */
    public ArrowPlanResult planClassicBorrowing(BoatConfig config, List<Integer> arrowWaves) {
        Map<String, Integer> uses = new HashMap<>();
        List<String> directions = new ArrayList<>();
        List<Integer> received = new ArrayList<>();

        for (int arrows : arrowWaves) {
            String bestDirection = null;
            int bestArrows = -1;
            for (Map.Entry<String, Integer> entry : config.strawMenByDirection().entrySet()) {
                int useCount = uses.getOrDefault(entry.getKey(), 0);
                if (useCount >= 3) {
                    continue;
                }
                // 根据该方向已经使用的次数计算剩余捕获效率。
                double efficiency = switch (useCount) {
                    case 0 -> 1.0;
                    case 1 -> 0.8;
                    default -> 0.4;
                };
                int captured = (int) Math.floor(arrows * (entry.getValue() * efficiency / 100.0));
                if (captured > bestArrows) {
                    bestArrows = captured;
                    bestDirection = entry.getKey();
                }
            }
            directions.add(bestDirection);
            received.add(bestArrows);
            // 只有选中的方向会增加使用次数，影响后续轮次效率。
            uses.put(bestDirection, uses.getOrDefault(bestDirection, 0) + 1);
        }

        return new ArrowPlanResult(directions, received, received.stream().mapToInt(Integer::intValue).sum());
    }

    /**
     * 扩展规则：箭雨可以无序，每个方向最多使用两次，第二次效率为 50%。
     */
    public ArrowPlanResult planDynamicBorrowing(BoatConfig config, List<Integer> arrowWaves) {
        Map<String, Integer> uses = new HashMap<>();
        List<String> directions = new ArrayList<>();
        List<Integer> received = new ArrayList<>();

        for (int arrows : arrowWaves) {
            String bestDirection = "skip";
            int bestArrows = 0;
            for (Map.Entry<String, Integer> entry : config.strawMenByDirection().entrySet()) {
                int useCount = uses.getOrDefault(entry.getKey(), 0);
                if (useCount >= 2) {
                    continue;
                }
                double efficiency = useCount == 0 ? 1.0 : 0.5;
                int captured = (int) Math.floor(arrows * (entry.getValue() * efficiency / 100.0));
                if (captured > bestArrows) {
                    bestArrows = captured;
                    bestDirection = entry.getKey();
                }
            }
            directions.add(bestDirection);
            received.add(bestArrows);
            if (!"skip".equals(bestDirection)) {
                // skip 表示本轮不接箭，不应消耗任何方向的使用次数。
                uses.put(bestDirection, uses.getOrDefault(bestDirection, 0) + 1);
            }
        }

        return new ArrowPlanResult(directions, received, received.stream().mapToInt(Integer::intValue).sum());
    }
}
