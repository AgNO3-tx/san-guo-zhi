package sunwu.ui;

import sunwu.domain.Point;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GUI 播放器中的一个算法演示步骤。
 */
public record VisualizationStep(
    String title,
    String detail,
    Set<Integer> currentNodes,
    Set<Integer> visitedNodes,
    Set<Integer> candidateNodes,
    List<Integer> pathNodes,
    Set<String> currentEdges,
    Set<Point> currentCells,
    Set<Point> visitedCells,
    Set<Point> selectedCells,
    Map<Point, Integer> clusterCells,
    String boatDirection,
    int totalValue,
    Map<String, Integer> metrics
) {
    public VisualizationStep {
        title = title == null ? "" : title;
        detail = detail == null ? "" : detail;
        currentNodes = copySet(currentNodes);
        visitedNodes = copySet(visitedNodes);
        candidateNodes = copySet(candidateNodes);
        pathNodes = pathNodes == null ? List.of() : List.copyOf(pathNodes);
        currentEdges = copySet(currentEdges);
        currentCells = copySet(currentCells);
        visitedCells = copySet(visitedCells);
        selectedCells = copySet(selectedCells);
        clusterCells = clusterCells == null ? Map.of() : Map.copyOf(clusterCells);
        boatDirection = boatDirection == null ? "" : boatDirection;
        metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
    }

    public static Builder builder(String title, String detail) {
        return new Builder(title, detail);
    }

    private static <T> Set<T> copySet(Set<T> source) {
        return source == null ? Set.of() : Set.copyOf(source);
    }

    public static final class Builder {
        private final String title;
        private final String detail;
        private final Set<Integer> currentNodes = new LinkedHashSet<>();
        private final Set<Integer> visitedNodes = new LinkedHashSet<>();
        private final Set<Integer> candidateNodes = new LinkedHashSet<>();
        private List<Integer> pathNodes = List.of();
        private final Set<String> currentEdges = new LinkedHashSet<>();
        private final Set<Point> currentCells = new LinkedHashSet<>();
        private final Set<Point> visitedCells = new LinkedHashSet<>();
        private final Set<Point> selectedCells = new LinkedHashSet<>();
        private final Map<Point, Integer> clusterCells = new LinkedHashMap<>();
        private String boatDirection = "";
        private int totalValue;
        private final Map<String, Integer> metrics = new LinkedHashMap<>();

        private Builder(String title, String detail) {
            this.title = title;
            this.detail = detail;
        }

        public Builder currentNode(int node) {
            currentNodes.add(node);
            return this;
        }

        public Builder visitedNodes(Set<Integer> nodes) {
            visitedNodes.addAll(nodes);
            return this;
        }

        public Builder candidateNode(int node) {
            candidateNodes.add(node);
            return this;
        }

        public Builder pathNodes(List<Integer> nodes) {
            pathNodes = nodes == null ? List.of() : List.copyOf(nodes);
            return this;
        }

        public Builder edge(int from, int to) {
            currentEdges.add(edgeKey(from, to));
            return this;
        }

        public Builder currentCell(Point point) {
            currentCells.add(point);
            return this;
        }

        public Builder visitedCells(Set<Point> points) {
            visitedCells.addAll(points);
            return this;
        }

        public Builder selectedCell(Point point) {
            selectedCells.add(point);
            return this;
        }

        public Builder clusterCells(Map<Point, Integer> cells) {
            clusterCells.putAll(cells);
            return this;
        }

        public Builder boatDirection(String direction) {
            boatDirection = direction;
            return this;
        }

        public Builder totalValue(int value) {
            totalValue = value;
            return this;
        }

        public Builder metric(String name, int value) {
            metrics.put(name, value);
            return this;
        }

        public VisualizationStep build() {
            return new VisualizationStep(
                title,
                detail,
                currentNodes,
                visitedNodes,
                candidateNodes,
                pathNodes,
                currentEdges,
                currentCells,
                visitedCells,
                selectedCells,
                clusterCells,
                boatDirection,
                totalValue,
                metrics
            );
        }
    }

    public static String edgeKey(int from, int to) {
        return from + "-" + to;
    }
}
