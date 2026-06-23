package sunwu.ui;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.General;
import sunwu.service.ArrowBorrowingService;
import sunwu.service.BattlefieldPathService;
import sunwu.service.CipherService;
import sunwu.service.ClusterFireService;
import sunwu.service.FoodHarvestService;
import sunwu.service.GeneralAnalyticsService;
import sunwu.service.HierarchyService;
import sunwu.service.MazeEscapeService;
import sunwu.service.ReportFormatter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Swing 综合演示界面。
 * 新版 GUI 按英文 PDF 的 Basic Features / Extra Features 板块组织，而不是只堆文本标签页。
 */
public final class SunWuDashboard {
    private static final ReportFormatter FORMATTER = new ReportFormatter();
    private static final Font MONO_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    private static final String SAMPLE_CIPHER = "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.";
    private static SwingWorker<String, Void> activeWorker;
    private static SwingWorker<Optional<VisualizationTrace>, Void> activeVisualizationWorker;

    private SunWuDashboard() {
    }

    /**
     * 返回 GUI 使用的功能目录。测试也会读取这里，确保界面和作业要求保持一致。
     */
    public static List<DashboardFeature> featureCatalog() {
        return List.of(
            new DashboardFeature("Basic Feature", "1. Forming Wu Kingdom's Hierarchy", "1", "Hierarchy 权力层级树", "用树结构展示 Sun Quan、两位 chief 和普通武将的三层关系。"),
            new DashboardFeature("Basic Feature", "2. Soldier's Arrangement", "2, 3", "Soldier Arrangement 排序/查找/组队", "按能力排序、二分查找，并枚举三人队伍做 S/A/B/C 评级。"),
            new DashboardFeature("Basic Feature", "3. Borrowing Arrows with Straw Boats", "4", "Straw Boats 原题草船借箭", "按原题效率衰减规则选择每轮最佳船身朝向。"),
            new DashboardFeature("Basic Feature", "4. Enemy Fortress Attack Simulation", "6", "Fortress Attack BFS 敌营路径", "从 Node 1 出发，用 BFS 找到到敌营节点的所有最短路径。"),
            new DashboardFeature("Basic Feature", "5. Food Harvesting", "7", "Food Harvesting 基础粮草路径", "根据无粮节点调整粮草采集路线。"),
            new DashboardFeature("Basic Feature", "6. Encrypted Text", "10", "Encrypted Text Caesar 解密", "处理 Caesar 移位和 ^、$、() 三种特殊语法。"),
            new DashboardFeature("Basic Feature", "7. Red Cliff on Fire", "12", "Red Cliff on Fire 集群统计", "用 8 连通规则统计战船矩阵中需要的火球数量。"),
            new DashboardFeature("Basic Feature", "8. Engaging Cao Cao at Hua Rong Road", "14", "Hua Rong Road 华容道路径", "用 BFS 找出曹操从迷宫起点到出口的逃跑路。"),
            new DashboardFeature("Extra Feature", "Graphic User Interface", "16", "GUI 综合演示台", "按作业板块提供交互式演示、参数输入和输出查看。"),
            new DashboardFeature("Extra Feature", "Extra Algorithm Implementation", "15", "Weighted Fortress 地形最短时间", "根据兵种和地形用 Dijkstra 计算最短时间路径。"),
            new DashboardFeature("Extra Feature", "Dynamic Arrow Borrowing", "5", "Dynamic Straw Boats 动态草船", "箭雨不再递减，每个方向最多使用两次。"),
            new DashboardFeature("Extra Feature", "Food Harvesting I / II", "8, 9", "Food Harvesting Extra 粮草扩展", "选择政治/智力队伍最大化粮草，并展示三将占营模拟。"),
            new DashboardFeature("Extra Feature", "Text Converter with More Secured Encryption", "11", "Secure Text Converter 增强加密", "使用 &num{} 扩展规则完成加密和解密回转。"),
            new DashboardFeature("Extra Feature", "Red Cliff on Fire with Optimized Points", "13", "Optimized Fire Points 最优投掷点", "为每个战船集群选择扩散轮数更少的火球坐标。")
        );
    }

    public static Optional<VisualizationTrace> visualizationTraceFor(DashboardFeature feature) {
        String pdfSection = feature.pdfSection();
        if (pdfSection.contains("Borrowing Arrows")) {
            return Optional.of(VisualizationTraceFactory.classicBoatTrace(List.of(2000, 1500, 1000, 800, 600, 500, 300, 300)));
        }
        if (pdfSection.contains("Enemy Fortress Attack Simulation") && feature.requirementType().equals("Basic Feature")) {
            return Optional.of(VisualizationTraceFactory.fortressBfsTrace(8));
        }
        if (pdfSection.equals("5. Food Harvesting")) {
            return Optional.of(VisualizationTraceFactory.foodHarvestTrace(Set.of(9)));
        }
        if (pdfSection.equals("7. Red Cliff on Fire")) {
            return Optional.of(VisualizationTraceFactory.fireClusterTrace());
        }
        if (pdfSection.contains("Hua Rong")) {
            return Optional.of(VisualizationTraceFactory.mazeTrace());
        }
        if (pdfSection.contains("Extra Algorithm")) {
            return Optional.of(VisualizationTraceFactory.weightedPathTrace("Xu Sheng", 8));
        }
        if (pdfSection.contains("Dynamic Arrow")) {
            return Optional.of(VisualizationTraceFactory.dynamicBoatTrace(List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400)));
        }
        if (pdfSection.contains("Food Harvesting I")) {
            return Optional.of(VisualizationTraceFactory.guardedCampTrace());
        }
        if (pdfSection.contains("Optimized Points")) {
            return Optional.of(VisualizationTraceFactory.optimizedFireTrace());
        }
        return Optional.empty();
    }

    public static void showWindow() {
        SwingUtilities.invokeLater(SunWuDashboard::createAndShow);
    }

    /**
     * 创建主窗口：左侧是作业板块目录，右侧是说明、控件和输出。
     */
    private static void createAndShow() {
        JFrame frame = new JFrame("Sun Wu Battle System - Assignment Demonstrator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1180, 760);
        frame.setLayout(new BorderLayout(8, 8));

        JTextArea outputArea = outputArea(buildOverviewText());
        JPanel detailPanel = new JPanel(new BorderLayout(8, 8));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel titleLabel = new JLabel("Sun Wu Battle System");
        JLabel metaLabel = new JLabel("Basic Features / Extra Features");
        JTextArea descriptionArea = outputArea("");
        descriptionArea.setRows(4);
        descriptionArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel visualizationPanel = new JPanel(new BorderLayout(8, 8));
        JPanel workPanel = new JPanel(new BorderLayout(8, 8));
        JPanel topWorkPanel = new JPanel(new BorderLayout(8, 8));
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.add(titleLabel);
        headerPanel.add(metaLabel);
        headerPanel.add(new JScrollPane(descriptionArea));

        detailPanel.add(headerPanel, BorderLayout.NORTH);
        topWorkPanel.add(controlPanel, BorderLayout.NORTH);
        topWorkPanel.add(visualizationPanel, BorderLayout.CENTER);
        workPanel.add(topWorkPanel, BorderLayout.NORTH);
        workPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        detailPanel.add(workPanel, BorderLayout.CENTER);

        JList<DashboardFeature> featureList = new JList<>(featureListModel());
        featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        featureList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        featureList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                DashboardFeature feature = featureList.getSelectedValue();
                if (feature != null) {
                    try {
                        cancelActiveWorker();
                        cancelActiveVisualizationWorker();
                        titleLabel.setText(feature.title());
                        metaLabel.setText(feature.requirementType() + " | PDF: " + feature.pdfSection() + " | Menu: " + feature.menuItems());
                        descriptionArea.setText(feature.description());
                        controlPanel.removeAll();
                        visualizationPanel.removeAll();
                        controlPanel.add(createControls(feature, outputArea, visualizationPanel), BorderLayout.NORTH);
                        runVisualization(visualizationPanel, () -> visualizationTraceFor(feature));
                        outputArea.setText(buildFeatureSummary(feature));
                        controlPanel.revalidate();
                        controlPanel.repaint();
                        visualizationPanel.revalidate();
                        visualizationPanel.repaint();
                    } catch (RuntimeException exception) {
                        outputArea.setText(formatError(exception));
                    }
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(featureList),
            detailPanel
        );
        splitPane.setDividerLocation(330);
        frame.add(splitPane, BorderLayout.CENTER);
        featureList.setSelectedIndex(0);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static DefaultListModel<DashboardFeature> featureListModel() {
        DefaultListModel<DashboardFeature> model = new DefaultListModel<>();
        featureCatalog().forEach(model::addElement);
        return model;
    }

    private static JTextArea outputArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(false);
        area.setFont(MONO_FONT);
        return area;
    }

    /**
     * 根据所选 PDF 板块创建对应输入控件。
     */
    private static JPanel createControls(DashboardFeature feature, JTextArea outputArea, JPanel visualizationPanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        String pdfSection = feature.pdfSection();

        if (pdfSection.contains("Hierarchy")) {
            addRunButton(panel, outputArea, "构建权力树", SunWuDashboard::buildHierarchyText);
        } else if (pdfSection.contains("Soldier")) {
            JComboBox<String> abilityBox = new JComboBox<>(new String[]{"Strength", "Leadership", "Intelligence", "Politic", "Hit Point"});
            JTextField targetField = new JTextField("98", 6);
            JTextField limitField = new JTextField("10", 4);
            panel.add(new JLabel("能力:"));
            panel.add(abilityBox);
            panel.add(new JLabel("查找值:"));
            panel.add(targetField);
            panel.add(new JLabel("排行数:"));
            panel.add(limitField);
            JButton runButton = new JButton("运行排序/查找/组队");
            runButton.addActionListener(event -> runFeature(outputArea, () -> buildSoldierArrangementText(
                abilityFromLabel((String) abilityBox.getSelectedItem()),
                parseIntOrDefault(targetField.getText(), 98),
                parseIntOrDefault(limitField.getText(), 10)
            )));
            panel.add(runButton);
        } else if (pdfSection.contains("Borrowing Arrows")) {
            JTextField waves = new JTextField("2000,1500,1000,800,600,500,300,300", 34);
            panel.add(new JLabel("箭雨:"));
            panel.add(waves);
            JButton runButton = new JButton("运行原题规则");
            runButton.addActionListener(event -> {
                List<Integer> parsedWaves = parseIntegerList(waves.getText(), List.of(2000, 1500, 1000, 800, 600, 500, 300, 300));
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.classicBoatTrace(parsedWaves)));
                runFeature(outputArea, () -> buildClassicArrowText(parsedWaves));
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Enemy Fortress Attack Simulation") && feature.requirementType().equals("Basic Feature")) {
            JTextField target = new JTextField("8", 5);
            panel.add(new JLabel("敌营节点:"));
            panel.add(target);
            JButton runButton = new JButton("运行 BFS");
            runButton.addActionListener(event -> {
                int parsedTarget = parseIntOrDefault(target.getText(), 8);
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.fortressBfsTrace(parsedTarget)));
                runFeature(outputArea, () -> buildFortressBfsText(parsedTarget));
            });
            panel.add(runButton);
        } else if (pdfSection.equals("5. Food Harvesting")) {
            JTextField noFood = new JTextField("9", 12);
            panel.add(new JLabel("无粮节点:"));
            panel.add(noFood);
            JButton runButton = new JButton("规划路线");
            runButton.addActionListener(event -> {
                Set<Integer> parsedNoFood = parseIntegerSet(noFood.getText(), Set.of(9));
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.foodHarvestTrace(parsedNoFood)));
                runFeature(outputArea, () -> buildFoodHarvestText(parsedNoFood));
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Encrypted Text")) {
            JTextField cipherText = new JTextField(SAMPLE_CIPHER, 42);
            JTextField shift = new JTextField("7", 4);
            panel.add(new JLabel("密文:"));
            panel.add(cipherText);
            panel.add(new JLabel("shift:"));
            panel.add(shift);
            JButton runButton = new JButton("解密");
            runButton.addActionListener(event -> runFeature(outputArea, () -> buildCipherText(cipherText.getText(), parseIntOrDefault(shift.getText(), 7))));
            panel.add(runButton);
        } else if (pdfSection.equals("7. Red Cliff on Fire")) {
            JButton runButton = new JButton("统计集群");
            runButton.addActionListener(event -> {
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.fireClusterTrace()));
                runFeature(outputArea, SunWuDashboard::buildFireClusterText);
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Hua Rong")) {
            JButton runButton = new JButton("搜索迷宫路径");
            runButton.addActionListener(event -> {
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.mazeTrace()));
                runFeature(outputArea, SunWuDashboard::buildMazeText);
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Graphic User Interface")) {
            addRunButton(panel, outputArea, "查看 GUI 覆盖范围", SunWuDashboard::buildOverviewText);
        } else if (pdfSection.contains("Extra Algorithm")) {
            JComboBox<String> generalBox = new JComboBox<>(generalNames());
            JTextField target = new JTextField("8", 5);
            panel.add(new JLabel("武将:"));
            panel.add(generalBox);
            panel.add(new JLabel("敌营节点:"));
            panel.add(target);
            JButton runButton = new JButton("运行 Dijkstra");
            runButton.addActionListener(event -> {
                String generalName = (String) generalBox.getSelectedItem();
                int parsedTarget = parseIntOrDefault(target.getText(), 8);
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.weightedPathTrace(generalName, parsedTarget)));
                runFeature(outputArea, () -> buildWeightedPathText(generalName, parsedTarget));
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Dynamic Arrow")) {
            JTextField waves = new JTextField("300,1500,1000,2000,600,800,300,500,400", 34);
            panel.add(new JLabel("箭雨:"));
            panel.add(waves);
            JButton runButton = new JButton("运行动态规则");
            runButton.addActionListener(event -> {
                List<Integer> parsedWaves = parseIntegerList(waves.getText(), List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400));
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.dynamicBoatTrace(parsedWaves)));
                runFeature(outputArea, () -> buildDynamicArrowText(parsedWaves));
            });
            panel.add(runButton);
        } else if (pdfSection.contains("Food Harvesting I")) {
            JComboBox<String> focusBox = new JComboBox<>(new String[]{"Politic", "Intelligence"});
            JTextField nodeCount = new JTextField("8", 5);
            panel.add(new JLabel("队伍属性:"));
            panel.add(focusBox);
            panel.add(new JLabel("粮草节点数:"));
            panel.add(nodeCount);
            JButton productionButton = new JButton("最大化产量");
            productionButton.addActionListener(event -> {
                AbilityType focus = abilityFromLabel((String) focusBox.getSelectedItem());
                int parsedNodeCount = parseIntOrDefault(nodeCount.getText(), 8);
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.foodProductionTrace(focus, parsedNodeCount)));
                runFeature(outputArea, () -> buildFoodProductionText(focus, parsedNodeCount));
            });
            panel.add(productionButton);
            JButton guardedButton = new JButton("三将占营模拟");
            guardedButton.addActionListener(event -> {
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.guardedCampTrace()));
                runFeature(outputArea, SunWuDashboard::buildGuardedCampText);
            });
            panel.add(guardedButton);
        } else if (pdfSection.contains("More Secured")) {
            JTextField plainText = new JTextField("Attack at dawn", 18);
            JTextField rule = new JTextField("3", 4);
            panel.add(new JLabel("明文:"));
            panel.add(plainText);
            panel.add(new JLabel("num:"));
            panel.add(rule);
            JButton runButton = new JButton("加密并解密");
            runButton.addActionListener(event -> runFeature(outputArea, () -> buildSecureCipherText(
                plainText.getText(),
                parseIntOrDefault(rule.getText(), 3)
            )));
            panel.add(runButton);
        } else if (pdfSection.contains("Optimized Points")) {
            JButton runButton = new JButton("计算最优投掷点");
            runButton.addActionListener(event -> {
                runVisualization(visualizationPanel, () -> Optional.of(VisualizationTraceFactory.optimizedFireTrace()));
                runFeature(outputArea, SunWuDashboard::buildOptimalFireText);
            });
            panel.add(runButton);
        }

        return panel;
    }

    private static void setVisualization(JPanel visualizationPanel, VisualizationTrace trace) {
        visualizationPanel.removeAll();
        visualizationPanel.add(new TracePlaybackPanel(trace), BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }

    private static void runVisualization(JPanel visualizationPanel, Supplier<Optional<VisualizationTrace>> supplier) {
        cancelActiveVisualizationWorker();
        visualizationPanel.removeAll();
        visualizationPanel.add(new JLabel("Preparing visualization..."), BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
        SwingWorker<Optional<VisualizationTrace>, Void> worker = new SwingWorker<>() {
            @Override
            protected Optional<VisualizationTrace> doInBackground() {
                return supplier.get();
            }

            @Override
            protected void done() {
                if (isCancelled() || activeVisualizationWorker != this) {
                    return;
                }
                try {
                    Optional<VisualizationTrace> trace = get();
                    if (trace.isPresent()) {
                        setVisualization(visualizationPanel, trace.get());
                    } else {
                        visualizationPanel.removeAll();
                        visualizationPanel.add(new JLabel("No process visualization for this module."), BorderLayout.CENTER);
                        visualizationPanel.revalidate();
                        visualizationPanel.repaint();
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    visualizationPanel.removeAll();
                    visualizationPanel.add(new JLabel("Visualization failed: " + exception.getCause().getMessage()), BorderLayout.CENTER);
                    visualizationPanel.revalidate();
                    visualizationPanel.repaint();
                }
            }
        };
        activeVisualizationWorker = worker;
        worker.execute();
    }

    private static void addRunButton(JPanel panel, JTextArea outputArea, String label, Supplier<String> supplier) {
        JButton button = new JButton(label);
        button.addActionListener(event -> runFeature(outputArea, supplier));
        panel.add(button);
    }

    /**
     * 按钮触发的算法统一放到后台线程，避免 Swing 窗口在演示时假死。
     */
    private static void runFeature(JTextArea outputArea, Supplier<String> supplier) {
        cancelActiveWorker();
        outputArea.setText("Running...\n");
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return supplier.get();
            }

            @Override
            protected void done() {
                if (isCancelled() || activeWorker != this) {
                    return;
                }
                try {
                    outputArea.setText(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    outputArea.setText("Operation interrupted.\n");
                } catch (ExecutionException exception) {
                    outputArea.setText(formatError(exception.getCause()));
                }
            }
        };
        activeWorker = worker;
        worker.execute();
    }

    private static void cancelActiveWorker() {
        if (activeWorker != null && !activeWorker.isDone()) {
            activeWorker.cancel(true);
        }
    }

    private static void cancelActiveVisualizationWorker() {
        if (activeVisualizationWorker != null && !activeVisualizationWorker.isDone()) {
            activeVisualizationWorker.cancel(true);
        }
    }

    private static String formatError(Throwable throwable) {
        return "运行该功能时出现错误: " + throwable.getClass().getSimpleName() + "\n"
            + throwable.getMessage() + "\n";
    }

    public static String buildOverviewText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Sun Wu Battle System - Assignment Coverage\n");
        builder.append("============================================================\n");
        builder.append("Basic Features\n");
        featureCatalog().stream()
            .filter(feature -> feature.requirementType().equals("Basic Feature"))
            .forEach(feature -> builder.append("- Menu ")
                .append(feature.menuItems())
                .append(" | ")
                .append(feature.pdfSection())
                .append(" | ")
                .append(feature.title())
                .append("\n"));
        builder.append("\nExtra Features\n");
        featureCatalog().stream()
            .filter(feature -> feature.requirementType().equals("Extra Feature"))
            .forEach(feature -> builder.append("- Menu ")
                .append(feature.menuItems())
                .append(" | ")
                .append(feature.pdfSection())
                .append(" | ")
                .append(feature.title())
                .append("\n"));
        return builder.toString();
    }

    public static String buildFeatureSummary(DashboardFeature feature) {
        return feature.title() + "\n"
            + "============================================================\n"
            + "Requirement: " + feature.requirementType() + "\n"
            + "PDF Section: " + feature.pdfSection() + "\n"
            + "Console Menu: " + feature.menuItems() + "\n\n"
            + feature.description() + "\n\n"
            + "点击上方按钮运行该板块的当前实现。\n";
    }

    public static String buildHierarchyText() {
        return FORMATTER.formatHierarchy(new HierarchyService().buildHierarchy(SampleData.generals()));
    }

    public static String buildGeneralText() {
        return buildSoldierArrangementText(AbilityType.POLITIC, 99, 10);
    }

    public static String buildSoldierArrangementText(AbilityType ability, int target, int limit) {
        GeneralAnalyticsService service = new GeneralAnalyticsService();
        StringBuilder builder = new StringBuilder();
        builder.append(FORMATTER.formatRoster(SampleData.generals())).append("\n");
        builder.append(FORMATTER.formatSortedGenerals(SampleData.generals(), ability)).append("\n");
        builder.append("Binary search result for ")
            .append(FORMATTER.label(ability))
            .append(" = ")
            .append(target)
            .append(": ")
            .append(service.searchByAbility(SampleData.generals(), ability, target))
            .append("\n\n");
        builder.append(FORMATTER.formatTeamSuggestion(service.bestTeamForAbility(SampleData.generals(), ability)));
        builder.append("\nTeam ranking\n");
        builder.append(FORMATTER.formatTeamRankings(service.rankTeamsForAbility(SampleData.generals(), ability), limit));
        return builder.toString();
    }

    public static String buildArrowText() {
        return buildClassicArrowText(List.of(2000, 1500, 1000, 800, 600, 500, 300, 300))
            + "\n"
            + buildDynamicArrowText(List.of(300, 1500, 1000, 2000, 600, 800, 300, 500, 400));
    }

    public static String buildClassicArrowText(List<Integer> waves) {
        ArrowBorrowingService service = new ArrowBorrowingService();
        return FORMATTER.formatArrowPlan(
            "Classic Straw Boat Borrowing",
            service.planClassicBorrowing(SampleData.classicBoatConfig(), waves)
        );
    }

    public static String buildDynamicArrowText(List<Integer> waves) {
        ArrowBorrowingService service = new ArrowBorrowingService();
        return FORMATTER.formatArrowPlan(
            "Dynamic Straw Boat Borrowing",
            service.planDynamicBorrowing(SampleData.dynamicBoatConfig(), waves)
        );
    }

    public static String buildBattlefieldText() {
        return buildFortressBfsText(8) + "\n" + buildWeightedPathText("Xu Sheng", 8);
    }

    public static String buildFortressBfsText(int target) {
        BattlefieldPathService service = new BattlefieldPathService();
        return FORMATTER.formatPaths(
            "BFS shortest paths to node " + target,
            service.findShortestPaths(SampleData.battlefieldGraph(), 1, target).paths()
        );
    }

    public static String buildWeightedPathText(String generalName, int target) {
        BattlefieldPathService service = new BattlefieldPathService();
        General general = findGeneral(generalName);
        return "Weighted terrain path for " + general.name() + " [" + general.armyType() + "]\n"
            + FORMATTER.formatWeightedPath(service.findShortestTimePath(SampleData.weightedBattlefieldGraph(), general, target));
    }

    public static String buildFoodText() {
        return buildFoodHarvestText(Set.of(9))
            + "\n"
            + buildFoodProductionText(AbilityType.POLITIC, 8)
            + "\n"
            + buildGuardedCampText();
    }

    public static String buildFoodHarvestText(Set<Integer> noFoodNodes) {
        FoodHarvestService service = new FoodHarvestService();
        return "Food harvesting without nodes " + noFoodNodes + "\n"
            + FORMATTER.formatFoodSimulation(service.planFoodHarvest(SampleData.battlefieldGraph(), noFoodNodes));
    }

    public static String buildFoodProductionText(AbilityType focus, int nodeCount) {
        FoodHarvestService service = new FoodHarvestService();
        return "Food Harvesting I - focus on " + FORMATTER.label(focus) + "\n"
            + FORMATTER.formatFoodSimulation(service.maximizeFoodProduction(SampleData.generals(), focus, nodeCount));
    }

    public static String buildGuardedCampText() {
        FoodHarvestService service = new FoodHarvestService();
        return "Food Harvesting II - guarded camps\n"
            + FORMATTER.formatFoodSimulation(service.planGuardedCampSimulation(SampleData.weightedBattlefieldGraph(), SampleData.generals()));
    }

    public static String buildCipherText() {
        return buildCipherText(SAMPLE_CIPHER, 7)
            + "\n"
            + buildSecureCipherText("Attack at dawn", 3);
    }

    public static String buildCipherText(String encrypted, int shift) {
        CipherService service = new CipherService();
        return "Classic Caesar decrypt\n"
            + "Encoded: " + encrypted + "\n"
            + "Shift: " + shift + "\n"
            + "Decoded: " + service.decryptClassic(encrypted, shift).decoded() + "\n";
    }

    public static String buildSecureCipherText(String plainText, int rule) {
        CipherService service = new CipherService();
        String encrypted = service.encryptSecure(plainText, rule);
        return "Secure extension\n"
            + "Plain: " + plainText + "\n"
            + "Encrypted: " + encrypted + "\n"
            + "Decrypted: " + service.decryptSecure(encrypted) + "\n";
    }

    public static String buildFireText() {
        return buildFireClusterText() + "\n" + buildOptimalFireText();
    }

    public static String buildFireClusterText() {
        ClusterFireService service = new ClusterFireService();
        return FORMATTER.formatClusterSummary(SampleData.simpleFireGrid(), service.countClusters(SampleData.simpleFireGrid()));
    }

    public static String buildOptimalFireText() {
        ClusterFireService service = new ClusterFireService();
        return FORMATTER.formatClusterSummary(SampleData.optimizedFireGrid(), service.findOptimalIgnitionPoints(SampleData.optimizedFireGrid()));
    }

    public static String buildMazeText() {
        MazeEscapeService service = new MazeEscapeService();
        return FORMATTER.formatMazeWithPath(SampleData.huaRongMaze(), service.escape(SampleData.huaRongMaze()));
    }

    private static AbilityType abilityFromLabel(String label) {
        return switch (label) {
            case "Leadership" -> AbilityType.LEADERSHIP;
            case "Intelligence" -> AbilityType.INTELLIGENCE;
            case "Politic" -> AbilityType.POLITIC;
            case "Hit Point" -> AbilityType.HIT_POINT;
            default -> AbilityType.STRENGTH;
        };
    }

    private static int parseIntOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static List<Integer> parseIntegerList(String input, List<Integer> defaultValue) {
        try {
            List<Integer> parsed = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            return parsed.isEmpty() ? defaultValue : parsed;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static Set<Integer> parseIntegerSet(String input, Set<Integer> defaultValue) {
        try {
            Set<Integer> parsed = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
            return parsed.isEmpty() ? defaultValue : parsed;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static String[] generalNames() {
        return SampleData.generals().stream()
            .map(General::name)
            .toArray(String[]::new);
    }

    private static General findGeneral(String name) {
        return SampleData.generals().stream()
            .filter(general -> general.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseGet(() -> SampleData.generals().stream()
                .filter(general -> general.name().equals("Xu Sheng"))
                .findFirst()
                .orElseThrow());
    }
}
