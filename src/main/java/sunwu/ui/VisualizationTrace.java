package sunwu.ui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个模块完整的可播放算法轨迹。
 */
public record VisualizationTrace(
    String title,
    VisualizationKind kind,
    Map<Integer, Point> nodePositions,
    List<int[]> edges,
    int[][] grid,
    List<VisualizationStep> steps
) {
    public VisualizationTrace {
        title = title == null ? "" : title;
        kind = kind == null ? VisualizationKind.SCORE : kind;
        nodePositions = copyPositions(nodePositions);
        edges = edges == null ? List.of() : List.copyOf(edges);
        grid = copyGrid(grid);
        steps = steps == null ? List.of() : List.copyOf(steps);
    }

    public static VisualizationTrace graph(String title, Map<Integer, Point> nodePositions, List<int[]> edges, List<VisualizationStep> steps) {
        return new VisualizationTrace(title, VisualizationKind.GRAPH, nodePositions, edges, null, steps);
    }

    public static VisualizationTrace grid(String title, int[][] grid, List<VisualizationStep> steps) {
        return new VisualizationTrace(title, VisualizationKind.GRID, Map.of(), List.of(), grid, steps);
    }

    public static VisualizationTrace boat(String title, List<VisualizationStep> steps) {
        return new VisualizationTrace(title, VisualizationKind.BOAT, Map.of(), List.of(), null, steps);
    }

    public static VisualizationTrace score(String title, List<VisualizationStep> steps) {
        return new VisualizationTrace(title, VisualizationKind.SCORE, Map.of(), List.of(), null, steps);
    }

    private static Map<Integer, Point> copyPositions(Map<Integer, Point> source) {
        if (source == null) {
            return Map.of();
        }
        Map<Integer, Point> copy = new LinkedHashMap<>();
        source.forEach((node, point) -> copy.put(node, new Point(point)));
        return copy;
    }

    private static int[][] copyGrid(int[][] source) {
        if (source == null) {
            return new int[0][0];
        }
        int[][] copy = new int[source.length][];
        for (int row = 0; row < source.length; row++) {
            copy[row] = source[row].clone();
        }
        return copy;
    }
}
