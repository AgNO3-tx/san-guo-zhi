package sunwu.service;

import sunwu.domain.ArrowPlanResult;
import sunwu.domain.BoatConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArrowBorrowingService {
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
            uses.put(bestDirection, uses.getOrDefault(bestDirection, 0) + 1);
        }

        return new ArrowPlanResult(directions, received, received.stream().mapToInt(Integer::intValue).sum());
    }

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
                uses.put(bestDirection, uses.getOrDefault(bestDirection, 0) + 1);
            }
        }

        return new ArrowPlanResult(directions, received, received.stream().mapToInt(Integer::intValue).sum());
    }
}
