package sunwu.service;

import sunwu.domain.General;

import java.util.List;
import java.util.Map;

/**
 * 粮草功能的通用结果。
 * 不同粮草子功能会使用其中不同字段，格式化器按字段是否为空决定输出内容。
 */
public record FoodSimulationResult(
    List<Integer> path,
    List<General> assignedTeam,
    int totalFood,
    int totalCost,
    Map<String, List<Integer>> generalRoutes
) {
}
