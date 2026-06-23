package sunwu.ui;

import sunwu.domain.Point;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 纯 Swing 的算法步骤播放器。
 */
public final class TracePlaybackPanel extends JPanel {
    private static final int STEP_DELAY_MS = 900;

    private final VisualizationTrace trace;
    private final TraceCanvas canvas;
    private final JLabel stepLabel = new JLabel();
    private final Timer timer;
    private int stepIndex;

    public TracePlaybackPanel(VisualizationTrace trace) {
        this.trace = trace == null ? VisualizationTrace.score("No Visualization", List.of()) : trace;
        this.canvas = new TraceCanvas(trace);
        this.timer = new Timer(STEP_DELAY_MS, event -> advance());
        this.timer.setCoalesce(true);

        setLayout(new BorderLayout(8, 8));
        setPreferredSize(new Dimension(760, 320));

        JButton playButton = new JButton("播放");
        JButton pauseButton = new JButton("暂停");
        JButton nextButton = new JButton("下一步");
        JButton resetButton = new JButton("重置");
        playButton.addActionListener(event -> timer.start());
        pauseButton.addActionListener(event -> timer.stop());
        nextButton.addActionListener(event -> {
            timer.stop();
            advance();
        });
        resetButton.addActionListener(event -> reset());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.add(playButton);
        toolbar.add(pauseButton);
        toolbar.add(nextButton);
        toolbar.add(resetButton);
        toolbar.add(stepLabel);

        add(toolbar, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        showStep(0);
    }

    public static java.awt.Point scaleGraphPoint(java.awt.Point point, Iterable<java.awt.Point> allPoints, int width, int height) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (java.awt.Point candidate : allPoints) {
            minX = Math.min(minX, candidate.x);
            minY = Math.min(minY, candidate.y);
            maxX = Math.max(maxX, candidate.x);
            maxY = Math.max(maxY, candidate.y);
        }
        int margin = 42;
        int top = 38;
        int availableWidth = Math.max(1, width - margin * 2);
        int availableHeight = Math.max(1, height - top - margin);
        int sourceWidth = Math.max(1, maxX - minX);
        int sourceHeight = Math.max(1, maxY - minY);
        double scale = Math.min(availableWidth / (double) sourceWidth, availableHeight / (double) sourceHeight);
        int renderedWidth = (int) Math.round(sourceWidth * scale);
        int renderedHeight = (int) Math.round(sourceHeight * scale);
        int offsetX = margin + Math.max(0, (availableWidth - renderedWidth) / 2);
        int offsetY = top + Math.max(0, (availableHeight - renderedHeight) / 2);
        return new java.awt.Point(
            offsetX + (int) Math.round((point.x - minX) * scale),
            offsetY + (int) Math.round((point.y - minY) * scale)
        );
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    private void advance() {
        if (trace.steps().isEmpty()) {
            return;
        }
        if (stepIndex >= trace.steps().size() - 1) {
            timer.stop();
            return;
        }
        showStep(stepIndex + 1);
    }

    private void reset() {
        timer.stop();
        showStep(0);
    }

    private void showStep(int nextIndex) {
        stepIndex = Math.max(0, Math.min(nextIndex, Math.max(0, trace.steps().size() - 1)));
        VisualizationStep step = trace.steps().isEmpty()
            ? VisualizationStep.builder("No steps", "No visualization steps available.").build()
            : trace.steps().get(stepIndex);
        canvas.setStep(step);
        stepLabel.setText((stepIndex + 1) + "/" + Math.max(1, trace.steps().size()) + "  " + step.title() + " - " + step.detail());
    }

    private static final class TraceCanvas extends JPanel {
        private static final Color BACKGROUND = new Color(247, 249, 251);
        private static final Color EDGE = new Color(138, 148, 162);
        private static final Color PATH = new Color(214, 67, 67);
        private static final Color CURRENT = new Color(255, 199, 74);
        private static final Color CANDIDATE = new Color(116, 169, 255);
        private static final Color VISITED = new Color(188, 215, 191);
        private static final Color NODE_BORDER = new Color(64, 96, 130);
        private static final Color[] CLUSTER_COLORS = {
            new Color(240, 153, 110),
            new Color(113, 184, 146),
            new Color(121, 161, 224),
            new Color(203, 145, 214),
            new Color(222, 194, 96)
        };

        private final VisualizationTrace trace;
        private VisualizationStep step;

        private TraceCanvas(VisualizationTrace trace) {
            this.trace = trace == null ? VisualizationTrace.score("No Visualization", List.of()) : trace;
            setPreferredSize(new Dimension(720, 260));
            setBackground(BACKGROUND);
        }

        private void setStep(VisualizationStep step) {
            this.step = step;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawTitle(g2);
            if (step == null) {
                return;
            }
            switch (trace.kind()) {
                case GRAPH -> drawGraph(g2);
                case GRID -> drawGrid(g2);
                case BOAT -> drawBoat(g2);
                case SCORE -> drawScore(g2);
            }
        }

        private void drawTitle(Graphics2D g2) {
            g2.setColor(new Color(34, 42, 54));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            g2.drawString(trace.title(), 14, 22);
        }

        private void drawGraph(Graphics2D g2) {
            int radius = 18;
            for (int[] edge : trace.edges()) {
                java.awt.Point from = scaledPoint(edge[0]);
                java.awt.Point to = scaledPoint(edge[1]);
                if (from == null || to == null) {
                    continue;
                }
                String forward = VisualizationStep.edgeKey(edge[0], edge[1]);
                String backward = VisualizationStep.edgeKey(edge[1], edge[0]);
                boolean active = step.currentEdges().contains(forward) || step.currentEdges().contains(backward);
                g2.setColor(active ? CURRENT : EDGE);
                g2.setStroke(new BasicStroke(active ? 4f : 1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                drawGraphSegment(g2, from, to, edge[0], edge[1]);
            }

            drawPath(g2, step.pathNodes(), radius);

            for (Map.Entry<Integer, java.awt.Point> entry : trace.nodePositions().entrySet()) {
                int node = entry.getKey();
                java.awt.Point p = scaledPoint(node);
                Color fill = Color.WHITE;
                if (step.visitedNodes().contains(node)) {
                    fill = VISITED;
                }
                if (step.candidateNodes().contains(node)) {
                    fill = CANDIDATE;
                }
                if (step.currentNodes().contains(node)) {
                    fill = CURRENT;
                }
                if (step.pathNodes().contains(node)) {
                    fill = new Color(255, 224, 224);
                }
                g2.setColor(fill);
                g2.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
                g2.setColor(step.pathNodes().contains(node) ? PATH : NODE_BORDER);
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
                drawCenteredText(g2, String.valueOf(node), p.x, p.y + 5, new Font(Font.SANS_SERIF, Font.BOLD, 13), Color.BLACK);
            }
        }

        private void drawPath(Graphics2D g2, List<Integer> nodes, int radius) {
            if (nodes.size() < 2) {
                return;
            }
            g2.setColor(PATH);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int index = 0; index < nodes.size() - 1; index++) {
                java.awt.Point from = scaledPoint(nodes.get(index));
                java.awt.Point to = scaledPoint(nodes.get(index + 1));
                if (from != null && to != null) {
                    drawGraphSegment(g2, from, to, nodes.get(index), nodes.get(index + 1));
                }
            }
        }

        private void drawGraphSegment(Graphics2D g2, java.awt.Point from, java.awt.Point to, int fromNode, int toNode) {
            int dx = to.x - from.x;
            int dy = to.y - from.y;
            double distance = Math.hypot(dx, dy);
            if (distance < 85) {
                g2.drawLine(from.x, from.y, to.x, to.y);
                return;
            }
            double normalX = -dy / distance;
            double normalY = dx / distance;
            int direction = ((fromNode + toNode) % 2 == 0) ? 1 : -1;
            double curve = Math.min(38, Math.max(18, distance * 0.12)) * direction;
            double controlX = (from.x + to.x) / 2.0 + normalX * curve;
            double controlY = (from.y + to.y) / 2.0 + normalY * curve;
            g2.draw(new QuadCurve2D.Double(from.x, from.y, controlX, controlY, to.x, to.y));
        }

        private java.awt.Point scaledPoint(int node) {
            java.awt.Point point = trace.nodePositions().get(node);
            if (point == null) {
                return null;
            }
            return TracePlaybackPanel.scaleGraphPoint(point, trace.nodePositions().values(), getWidth(), getHeight());
        }

        private void drawGrid(Graphics2D g2) {
            int[][] grid = trace.grid();
            if (grid.length == 0) {
                return;
            }
            int rows = grid.length;
            int cols = grid[0].length;
            int cell = Math.max(28, Math.min((getWidth() - 80) / cols, (getHeight() - 70) / rows));
            int startX = (getWidth() - cols * cell) / 2;
            int startY = 42;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Point point = new Point(row, col);
                    Color fill = gridColor(grid[row][col]);
                    Integer cluster = step.clusterCells().get(point);
                    if (cluster != null) {
                        fill = CLUSTER_COLORS[(cluster - 1 + CLUSTER_COLORS.length) % CLUSTER_COLORS.length];
                    }
                    if (step.visitedCells().contains(point)) {
                        fill = mix(fill, VISITED);
                    }
                    if (step.selectedCells().contains(point)) {
                        fill = new Color(255, 105, 97);
                    }
                    if (step.currentCells().contains(point)) {
                        fill = CURRENT;
                    }

                    int x = startX + col * cell;
                    int y = startY + row * cell;
                    g2.setColor(fill);
                    g2.fillRect(x, y, cell, cell);
                    g2.setColor(new Color(98, 112, 130));
                    g2.drawRect(x, y, cell, cell);

                    String label = gridLabel(grid[row][col], cluster);
                    if (!label.isEmpty()) {
                        drawCenteredText(g2, label, x + cell / 2, y + cell / 2 + 5, new Font(Font.SANS_SERIF, Font.BOLD, 12), Color.BLACK);
                    }
                }
            }
        }

        private Color gridColor(int value) {
            return switch (value) {
                case 1 -> new Color(70, 80, 94);
                case 2 -> new Color(111, 203, 135);
                case 3 -> new Color(241, 105, 94);
                default -> new Color(235, 239, 244);
            };
        }

        private String gridLabel(int value, Integer cluster) {
            if (cluster != null && value == 1) {
                return String.valueOf(cluster);
            }
            return switch (value) {
                case 2 -> "S";
                case 3 -> "E";
                case 1 -> "";
                default -> "";
            };
        }

        private void drawBoat(Graphics2D g2) {
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(36, 75, 128));
            g2.fillRoundRect(24, 38, w - 48, h - 54, 8, 8);

            int cx = w / 2;
            int cy = h / 2 + 8;
            int boatW = 110;
            int boatH = 82;
            g2.setColor(new Color(140, 89, 45));
            g2.fillRoundRect(cx - boatW / 2, cy - boatH / 2, boatW, boatH, 12, 12);
            g2.setColor(new Color(88, 54, 28));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(cx - boatW / 2, cy - boatH / 2, boatW, boatH, 12, 12);
            drawCenteredText(g2, "草船", cx, cy + 4, new Font(Font.SANS_SERIF, Font.BOLD, 14), Color.WHITE);

            drawDirection(g2, "front", "前", cx, cy - 95, cx, cy - boatH / 2);
            drawDirection(g2, "left", "左", cx - 150, cy, cx - boatW / 2, cy);
            drawDirection(g2, "right", "右", cx + 150, cy, cx + boatW / 2, cy);
            drawDirection(g2, "back", "后", cx, cy + 95, cx, cy + boatH / 2);

            drawCenteredText(g2, "累计: " + step.totalValue() + " 支", cx, h - 18, new Font(Font.SANS_SERIF, Font.BOLD, 13), new Color(255, 235, 160));
        }

        private void drawDirection(Graphics2D g2, String id, String label, int x, int y, int targetX, int targetY) {
            boolean selected = id.equalsIgnoreCase(step.boatDirection());
            g2.setColor(selected ? CURRENT : new Color(228, 234, 240));
            g2.fillOval(x - 22, y - 22, 44, 44);
            g2.setColor(selected ? new Color(255, 109, 84) : new Color(83, 97, 116));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x - 22, y - 22, 44, 44);
            drawCenteredText(g2, label, x, y + 5, new Font(Font.SANS_SERIF, Font.BOLD, 14), Color.BLACK);
            if (selected) {
                g2.setColor(new Color(255, 218, 86));
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x, y, targetX, targetY);
            }
        }

        private void drawScore(Graphics2D g2) {
            int x = 60;
            int y = 64;
            int width = getWidth() - 120;
            for (Map.Entry<String, Integer> entry : step.metrics().entrySet()) {
                int value = entry.getValue();
                int barWidth = Math.max(20, Math.min(width, value));
                g2.setColor(new Color(220, 227, 236));
                g2.fillRoundRect(x, y, width, 22, 8, 8);
                g2.setColor(new Color(94, 137, 204));
                g2.fillRoundRect(x, y, barWidth, 22, 8, 8);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                g2.drawString(entry.getKey() + ": " + value, x, y - 6);
                y += 48;
            }
            drawWrappedDetail(g2);
        }

        private void drawWrappedDetail(Graphics2D g2) {
            g2.setColor(new Color(48, 57, 69));
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            g2.drawString(step.detail(), 30, getHeight() - 26);
        }

        private void drawCenteredText(Graphics2D g2, String text, int x, int y, Font font, Color color) {
            g2.setFont(font);
            g2.setColor(color);
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(text, x - metrics.stringWidth(text) / 2, y);
        }

        private Color mix(Color a, Color b) {
            return new Color((a.getRed() + b.getRed()) / 2, (a.getGreen() + b.getGreen()) / 2, (a.getBlue() + b.getBlue()) / 2);
        }
    }
}
