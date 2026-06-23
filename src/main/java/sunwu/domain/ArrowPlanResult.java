package sunwu.domain;

import java.util.List;

/**
 * 草船借箭规划结果：记录每一轮朝向、每轮获得箭数和总箭数。
 */
public record ArrowPlanResult(
    List<String> directions,
    List<Integer> arrowsReceived,
    int totalArrows
) {
}
