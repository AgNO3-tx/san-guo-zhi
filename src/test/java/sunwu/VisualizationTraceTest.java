package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.ui.DashboardFeature;
import sunwu.ui.SunWuDashboard;
import sunwu.ui.VisualizationStep;
import sunwu.ui.TracePlaybackPanel;
import sunwu.ui.VisualizationTrace;
import sunwu.ui.VisualizationTraceFactory;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 验证 GUI 可视化不是只展示最终结果，而是能生成可播放的算法过程步骤。
 */
public final class VisualizationTraceTest {
    private VisualizationTraceTest() {
    }

    public static void run() {
        assertTraceHasSteps(
            VisualizationTraceFactory.fortressBfsTrace(8).steps(),
            "BFS trace should include step-by-step graph traversal."
        );
        TestSupport.assertTrue(
            VisualizationTraceFactory.fortressBfsTrace(8).steps().stream()
                .anyMatch(step -> step.detail().contains("加入队列")),
            "BFS trace should show neighbor enqueue decisions."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.foodHarvestTrace(Set.of(9)).steps(),
            "Food harvesting trace should include route filtering steps."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.mazeTrace().steps(),
            "Maze trace should include BFS expansion steps."
        );
        TestSupport.assertTrue(
            VisualizationTraceFactory.mazeTrace().steps().stream()
                .anyMatch(step -> step.detail().contains("到达出口")),
            "Maze trace should show when the exit is reached."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.weightedPathTrace("Xu Sheng", 8).steps(),
            "Weighted path trace should include terrain edge evaluation steps."
        );
        TestSupport.assertTrue(
            VisualizationTraceFactory.weightedPathTrace("Xu Sheng", 8).steps().stream()
                .anyMatch(step -> step.detail().contains("耗时")),
            "Weighted path trace should show travel-time decisions."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.fireClusterTrace().steps(),
            "Fire cluster trace should include cluster discovery steps."
        );
        TestSupport.assertTrue(
            VisualizationTraceFactory.fireClusterTrace().steps().stream()
                .anyMatch(step -> step.title().contains("集群")),
            "Fire trace should label cluster discovery."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.optimizedFireTrace().steps(),
            "Optimized fire trace should include candidate ignition steps."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.classicBoatTrace(List.of(2000, 1500, 1000)).steps(),
            "Classic boat trace should include arrow wave steps."
        );
        TestSupport.assertTrue(
            VisualizationTraceFactory.classicBoatTrace(List.of(2000, 1500, 1000)).steps().stream()
                .anyMatch(step -> step.detail().contains("累计")),
            "Boat trace should show cumulative arrows."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.dynamicBoatTrace(List.of(300, 1500, 1000)).steps(),
            "Dynamic boat trace should include arrow wave steps."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.foodProductionTrace(AbilityType.POLITIC, 8).steps(),
            "Food production trace should include team comparison steps."
        );

        assertTraceHasSteps(
            VisualizationTraceFactory.guardedCampTrace().steps(),
            "Guarded camp trace should include multi-general route steps."
        );

        List<DashboardFeature> visualizedFeatures = SunWuDashboard.featureCatalog().stream()
            .filter(VisualizationTraceTest::shouldHaveVisualization)
            .toList();
        TestSupport.assertEquals(9, visualizedFeatures.size(), "The expected dashboard entries should be covered by process visuals.");
        for (DashboardFeature feature : visualizedFeatures) {
            Optional<?> trace = SunWuDashboard.visualizationTraceFor(feature);
            TestSupport.assertTrue(trace.isPresent(), feature.title() + " should be wired to a visualization trace.");
        }

        TracePlaybackPanel panel = new TracePlaybackPanel(VisualizationTraceFactory.fortressBfsTrace(8));
        TestSupport.assertTrue(panel.getComponentCount() > 0, "Trace playback panel should create visible Swing child components.");
        TracePlaybackPanel emptyPanel = new TracePlaybackPanel(null);
        TestSupport.assertTrue(emptyPanel.getComponentCount() > 0, "Trace playback panel should tolerate a missing trace.");

        assertCanPaint(new TracePlaybackPanel(null), 760, 320, "Null trace panel should paint without throwing.");
        assertCanPaint(new TracePlaybackPanel(VisualizationTraceFactory.foodProductionTrace(AbilityType.POLITIC, 8)), 760, 320, "SCORE trace should paint without throwing.");
        assertCanPaint(new TracePlaybackPanel(scoreTraceWithEdgeMetrics()), 760, 320, "SCORE trace should tolerate zero, negative, and large metrics.");
        assertCanPaint(new TracePlaybackPanel(VisualizationTraceFactory.fortressBfsTrace(8)), 360, 220, "Graph trace should paint at a compact size.");
        assertCanPaint(new TracePlaybackPanel(VisualizationTraceFactory.fortressBfsTrace(8)), 1200, 520, "Graph trace should repaint after resize.");
        assertCanPaint(new TracePlaybackPanel(VisualizationTraceFactory.mazeTrace()), 760, 320, "Grid trace should paint without throwing.");
        assertCanPaint(new TracePlaybackPanel(VisualizationTraceFactory.classicBoatTrace(List.of())), 760, 320, "Empty boat trace should paint a final zero-result step.");
        assertRapidPlaybackClicksDoNotBreakPanel();
        assertMazeGridFitsCompactCanvas();
        assertBattlefieldLayoutKeepsMapAspect();
        assertBattlefieldLayoutMatchesAssignmentMap();
        assertDijkstraSettledTimesAreMonotonic();
        assertBfsFinalPathMatchesServiceResult();
        assertFoodTraceFinalPathMatchesServiceResult();
    }

    private static void assertTraceHasSteps(List<?> steps, String message) {
        TestSupport.assertTrue(!steps.isEmpty(), message);
    }

    private static boolean shouldHaveVisualization(DashboardFeature feature) {
        String section = feature.pdfSection();
        return section.contains("Borrowing Arrows")
            || section.contains("Enemy Fortress")
            || section.equals("5. Food Harvesting")
            || section.contains("Red Cliff on Fire")
            || section.contains("Hua Rong")
            || section.contains("Extra Algorithm")
            || section.contains("Dynamic Arrow")
            || section.contains("Food Harvesting I")
            || section.contains("Optimized Points");
    }

    private static VisualizationTrace scoreTraceWithEdgeMetrics() {
        return VisualizationTrace.score(
            "Score Edge Metrics",
            List.of(VisualizationStep.builder("Metrics", "Score renderer edge case metrics.")
                .metric("zero", 0)
                .metric("negative", -10)
                .metric("large", 5000)
                .build())
        );
    }

    private static void assertCanPaint(JComponent component, int width, int height, String message) {
        runOnEdt(() -> {
            component.setSize(width, height);
            component.doLayout();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try {
                component.paint(graphics);
            } finally {
                graphics.dispose();
            }
        }, message);
    }

    private static void assertRapidPlaybackClicksDoNotBreakPanel() {
        TracePlaybackPanel panel = new TracePlaybackPanel(VisualizationTraceFactory.fortressBfsTrace(8));
        runOnEdt(() -> {
            panel.setSize(760, 320);
            panel.doLayout();
            for (String label : List.of("播放", "暂停", "下一步", "播放", "下一步", "暂停", "重置")) {
                JButton button = findButton(panel, label);
                TestSupport.assertTrue(button != null, "Playback button should exist: " + label);
                button.doClick();
            }
            TestSupport.assertTrue(panel.getComponentCount() > 0, "Rapid playback clicks should keep panel components intact.");
        }, "Rapid playback clicks should not throw.");
    }

    private static void assertMazeGridFitsCompactCanvas() {
        int rows = SampleData.huaRongMaze().length;
        int cols = SampleData.huaRongMaze()[0].length;
        int cell = TracePlaybackPanel.gridCellSize(cols, rows, 720, 260);
        int startY = TracePlaybackPanel.gridStartY(rows, cell, 260);
        TestSupport.assertTrue(
            startY + rows * cell <= 260,
            "Maze visualization should fit all rows inside the canvas instead of being clipped by the output area."
        );
    }

    private static JButton findButton(Container container, String label) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button && label.equals(button.getText())) {
                return button;
            }
            if (component instanceof Container child) {
                JButton found = findButton(child, label);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void assertDijkstraSettledTimesAreMonotonic() {
        int previous = Integer.MIN_VALUE;
        for (VisualizationStep step : VisualizationTraceFactory.weightedPathTrace("Xu Sheng", 8).steps()) {
            if (step.title().contains("确定当前最短节点")) {
                int current = step.metrics().getOrDefault("time", previous);
                TestSupport.assertTrue(current >= previous, "Dijkstra settled-node times should be monotonic.");
                previous = current;
            }
        }
    }

    private static void assertBattlefieldLayoutKeepsMapAspect() {
        Map<Integer, Point> positions = VisualizationTraceFactory.foodHarvestTrace(Set.of(9)).nodePositions();
        List<Point> scaled = new ArrayList<>();
        for (Point point : positions.values()) {
            scaled.add(TracePlaybackPanel.scaleGraphPoint(point, positions.values(), 1580, 325));
        }
        int minX = scaled.stream().mapToInt(point -> point.x).min().orElse(0);
        int maxX = scaled.stream().mapToInt(point -> point.x).max().orElse(0);
        int minY = scaled.stream().mapToInt(point -> point.y).min().orElse(0);
        int maxY = scaled.stream().mapToInt(point -> point.y).max().orElse(0);
        double aspect = (maxX - minX) / (double) Math.max(1, maxY - minY);
        TestSupport.assertTrue(aspect < 2.5, "Battlefield layout should keep a map-like aspect instead of flattening into a line. Aspect: " + aspect);
    }

    private static void assertBattlefieldLayoutMatchesAssignmentMap() {
        Map<Integer, Point> positions = VisualizationTraceFactory.fortressBfsTrace(8).nodePositions();
        TestSupport.assertTrue(positions.get(1).y > positions.get(3).y, "Node 1 should sit below Node 3 like the assignment map.");
        TestSupport.assertTrue(positions.get(7).x > positions.get(6).x, "Node 7 should sit to the right of Node 6 like the assignment map.");
        TestSupport.assertTrue(positions.get(10).y > positions.get(8).y, "Node 10 should sit below Node 8 like the assignment map.");
        TestSupport.assertTrue(positions.get(5).y < positions.get(6).y, "Node 5 should sit above Node 6 like the assignment map.");
    }

    private static void assertBfsFinalPathMatchesServiceResult() {
        List<Integer> visualPath = VisualizationTraceFactory.fortressBfsTrace(8).steps().getLast().pathNodes();
        List<Integer> expectedPath = new sunwu.service.BattlefieldPathService()
            .findShortestPaths(SampleData.battlefieldGraph(), 1, 8)
            .paths()
            .getFirst();
        TestSupport.assertEquals(expectedPath, visualPath, "BFS visualization final path should match the service result.");
    }

    private static void assertFoodTraceFinalPathMatchesServiceResult() {
        List<Integer> visualPath = VisualizationTraceFactory.foodHarvestTrace(Set.of(9)).steps().getLast().pathNodes();
        List<Integer> expectedPath = new sunwu.service.FoodHarvestService()
            .planFoodHarvest(SampleData.battlefieldGraph(), Set.of(9))
            .path();
        TestSupport.assertEquals(expectedPath, visualPath, "Food harvesting visualization final path should match the service result.");
    }

    private static void runOnEdt(Runnable runnable, String message) {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    failure.set(throwable);
                }
            });
        } catch (Exception exception) {
            throw new AssertionError(message + " " + exception.getMessage());
        }
        if (failure.get() != null) {
            throw new AssertionError(message + " " + failure.get().getMessage());
        }
    }
}
