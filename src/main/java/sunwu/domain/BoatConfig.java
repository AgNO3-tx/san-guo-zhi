package sunwu.domain;

import java.util.Map;

/**
 * 草船四个方向的草人数量配置。
 */
public record BoatConfig(
    Map<String, Integer> strawMenByDirection
) {
}
