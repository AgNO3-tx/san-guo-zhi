package sunwu.domain;

/**
 * 带地形的有向边，to 是目标节点，terrain 是通过该边时的地形。
 */
public record Edge(
    int to,
    TerrainType terrain
) {
}
