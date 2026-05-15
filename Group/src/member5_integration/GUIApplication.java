package member5_integration;

import member1_character.*;
import member2_strategy.*;
import member3_graph.*;
import member4_battlefield.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

/**
 * GUI图形界面 - 可视化所有功能模块
 */
public class GUIApplication extends JFrame {
    private JTabbedPane tabbedPane;
    private List<member1_character.Character> characters;
    private JTextArea outputArea;

    public GUIApplication() {
        super("三国·赤壁之战 - 综合系统");
        characters = DataInitializer.initWuCharacters();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // 添加各功能标签页
        tabbedPane.addTab("武将体系", createCharacterPanel());
        tabbedPane.addTab("草船借箭", createBoatPanel());
        tabbedPane.addTab("凯撒密码", createCipherPanel());
        tabbedPane.addTab("敌营进攻", createBFSPanel());
        tabbedPane.addTab("粮草征收", createGrainPanel());
        tabbedPane.addTab("火烧赤壁", createFirePanel());
        tabbedPane.addTab("华容道迷宫", createMazePanel());
        tabbedPane.addTab("拓展功能", createExtensionPanel());

        // 输出区域
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("宋体", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(950, 200));

        // 主布局
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    /**
     * 运行一个任务并捕获其System.out输出
     */
    private String captureOutput(Runnable task) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(baos));
        task.run();
        System.setOut(original);
        return baos.toString();
    }

    // ========== 武将体系面板 ==========
    private JPanel createCharacterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 按钮面板
        JPanel btnPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        btnPanel.setBorder(new TitledBorder("武将体系操作"));

        JButton btnHierarchy = new JButton("组织结构图");
        JButton btnSort = new JButton("排序展示");
        JButton btnSearch = new JButton("二分查找");
        JButton btnGrade = new JButton("分级组队");
        JButton btnAll = new JButton("全部展示");

        btnPanel.add(btnHierarchy);
        btnPanel.add(btnSort);
        btnPanel.add(btnSearch);
        btnPanel.add(btnGrade);
        btnPanel.add(btnAll);

        // 武将列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (member1_character.Character c : characters) {
            listModel.addElement(c.toString());
        }
        JList<String> charList = new JList<>(listModel);
        charList.setFont(new Font("宋体", Font.PLAIN, 12));
        JScrollPane listScroll = new JScrollPane(charList);
        listScroll.setBorder(new TitledBorder("武将列表"));

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.CENTER);

        // 事件绑定
        btnHierarchy.addActionListener(e -> {
            TreeStructure tree = new TreeStructure();
            tree.buildTree(characters);
            String output = captureOutput(() -> {
                System.out.println(tree.getHierarchyString());
                tree.printDepartmentStats();
            });
            appendOutput(output);
        });

        btnSort.addActionListener(e -> {
            CharacterSorter sorter = new CharacterSorter();
            String output = captureOutput(() -> sorter.printAllSorts(characters));
            appendOutput(output);
        });

        btnSearch.addActionListener(e -> {
            String attr = JOptionPane.showInputDialog(this, "输入属性（领导力/武力/智力/政治/生命值）：");
            if (attr == null) return;
            String valStr = JOptionPane.showInputDialog(this, "输入值：");
            if (valStr == null) return;
            try {
                int value = Integer.parseInt(valStr);
                CharacterSorter sorter = new CharacterSorter();
                List<member1_character.Character> sorted = sorter.sortByAttribute(characters, attr);
                BinarySearch bs = new BinarySearch();
                member1_character.Character found = bs.search(sorted, attr, value);
                if (found != null) {
                    appendOutput("✅ 找到武将：" + found);
                } else {
                    appendOutput("❌ 未找到 " + attr + " = " + value + " 的武将");
                }
            } catch (NumberFormatException ex) {
                appendOutput("⚠ 请输入有效数字");
            }
        });

        btnGrade.addActionListener(e -> {
            TeamRecommender recommender = new TeamRecommender();
            String output = captureOutput(() -> {
                recommender.printGrades(characters);
                recommender.printTeamRecommendations(characters);
            });
            appendOutput(output);
        });

        btnAll.addActionListener(e -> {
            String output = captureOutput(() -> {
                TreeStructure tree = new TreeStructure();
                tree.buildTree(characters);
                System.out.println(tree.getHierarchyString());
                tree.printDepartmentStats();

                CharacterSorter sorter = new CharacterSorter();
                sorter.printAllSorts(characters);

                TeamRecommender recommender = new TeamRecommender();
                recommender.printGrades(characters);
                recommender.printTeamRecommendations(characters);
            });
            appendOutput(output);
        });

        return panel;
    }

    // ========== 草船借箭面板 ==========
    private JPanel createBoatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBorder(new TitledBorder("草船借箭"));

        JButton btnSimulate = new JButton("基础模拟");
        JButton btnDynamic = new JButton("动态模拟（拓展）");

        btnPanel.add(btnSimulate);
        btnPanel.add(btnDynamic);

        JTextArea infoArea = new JTextArea(5, 40);
        infoArea.setEditable(false);
        infoArea.setText("初始草人分布：前=80 左=60 右=70 后=50\n" +
                "基础版：每轮箭数递减，单方向最多3次\n" +
                "动态版：箭数随机，单方向最多2次");
        infoArea.setFont(new Font("宋体", Font.PLAIN, 13));

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        btnSimulate.addActionListener(e -> {
            GrassBoatSimulation boat = new GrassBoatSimulation();
            String output = captureOutput(() -> boat.simulate(DataInitializer.initScarecrows(), 8));
            appendOutput(output);
        });

        btnDynamic.addActionListener(e -> {
            GrassBoatSimulation boat = new GrassBoatSimulation();
            String output = captureOutput(() -> boat.dynamicSimulate(DataInitializer.initScarecrows(), 8));
            appendOutput(output);
        });

        return panel;
    }

    // ========== 凯撒密码面板 ==========
    private JPanel createCipherPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(new TitledBorder("凯撒密码解密"));

        inputPanel.add(new JLabel("密文："));
        JTextField cipherField = new JTextField();
        inputPanel.add(cipherField);
        inputPanel.add(new JLabel("偏移量："));
        JTextField shiftField = new JTextField("3");
        inputPanel.add(shiftField);
        JButton btnDecrypt = new JButton("解密");
        JButton btnExamples = new JButton("示例");
        inputPanel.add(btnDecrypt);
        inputPanel.add(btnExamples);

        JTextArea helpArea = new JTextArea(4, 40);
        helpArea.setEditable(false);
        helpArea.setText("特殊符号：\n^x → 后面字母大写\n$ → 空格\n(内容) → 括号内反转");
        helpArea.setFont(new Font("宋体", Font.PLAIN, 13));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(helpArea), BorderLayout.CENTER);

        btnDecrypt.addActionListener(e -> {
            String cipher = cipherField.getText().trim();
            if (cipher.isEmpty()) { appendOutput("⚠ 请输入密文"); return; }
            try {
                int shift = Integer.parseInt(shiftField.getText().trim());
                CaesarCipher cc = new CaesarCipher();
                String result = cc.decrypt(cipher, shift);
                appendOutput("密文: " + cipher + " | 偏移: " + shift + " | 明文: " + result);
            } catch (NumberFormatException ex) {
                appendOutput("⚠ 偏移量请输入数字");
            }
        });

        btnExamples.addActionListener(e -> {
            CaesarCipher cc = new CaesarCipher();
            String output = captureOutput(() -> cc.runExamples());
            appendOutput(output);
        });

        return panel;
    }

    // ========== BFS敌营进攻面板 ==========
    private JPanel createBFSPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnRun = new JButton("搜索所有可达敌营路径");
        btnRun.setFont(new Font("宋体", Font.PLAIN, 14));

        JTextArea infoArea = new JTextArea(8, 40);
        infoArea.setEditable(false);
        infoArea.setText("地图结构（有向图）：\n" +
                "Node 1 → 2, 3\n" +
                "Node 2 → 4, 5\n" +
                "Node 3 → 5, 6\n" +
                "Node 4 → 7\n" +
                "Node 5 → 7, 8\n" +
                "Node 6 → 8, 9\n" +
                "Node 7 → 10 (敌营)\n" +
                "Node 8 → 10 (敌营)\n" +
                "Node 9 → 10 (敌营)");
        infoArea.setFont(new Font("宋体", Font.PLAIN, 13));

        panel.add(btnRun, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        btnRun.addActionListener(e -> {
            BFSPathFinder finder = new BFSPathFinder(Graph.buildDefaultMap());
            String output = captureOutput(() -> finder.runDefault());
            appendOutput(output);
        });

        return panel;
    }

    // ========== 粮草征收面板 ==========
    private JPanel createGrainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnRun = new JButton("规划粮草征收路径");
        btnRun.setFont(new Font("宋体", Font.PLAIN, 14));

        JTextArea infoArea = new JTextArea(8, 40);
        infoArea.setEditable(false);
        infoArea.setText("粮草分布：\n" +
                "Node 1: 0 (起点)\n" +
                "Node 2: 50  Node 3: 30\n" +
                "Node 4: 0   Node 5: 80\n" +
                "Node 6: 60  Node 7: 40\n" +
                "Node 8: 0\n" +
                "目标：从Node1出发，收集所有粮草后返回");
        infoArea.setFont(new Font("宋体", Font.PLAIN, 13));

        panel.add(btnRun, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        btnRun.addActionListener(e -> {
            GrainCollector collector = new GrainCollector(
                    Graph.buildGrainMap(), Graph.getDefaultGrainMap());
            String output = captureOutput(() -> collector.runDefault());
            appendOutput(output);
        });

        return panel;
    }

    // ========== 火烧赤壁面板 ==========
    private JPanel createFirePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBorder(new TitledBorder("火烧赤壁"));

        JButton btnCluster = new JButton("统计战船集群");
        JButton btnOptimal = new JButton("最优火球投放（拓展）");

        btnPanel.add(btnCluster);
        btnPanel.add(btnOptimal);

        // 显示矩阵
        JTextArea gridArea = new JTextArea(8, 30);
        gridArea.setEditable(false);
        gridArea.setFont(new Font("宋体", Font.PLAIN, 14));
        int[][] grid = DataInitializer.initBattleGrid();
        StringBuilder sb = new StringBuilder("战船分布图：\n");
        for (int[] row : grid) {
            for (int val : row) {
                sb.append(val == 1 ? " ⛵" : " ·");
            }
            sb.append("\n");
        }
        gridArea.setText(sb.toString());

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(gridArea), BorderLayout.CENTER);

        btnCluster.addActionListener(e -> {
            FireAttack fire = new FireAttack();
            String output = captureOutput(() -> fire.printAnalysis(DataInitializer.initBattleGrid()));
            appendOutput(output);
        });

        btnOptimal.addActionListener(e -> {
            FireAttack fire = new FireAttack();
            String output = captureOutput(() -> fire.findOptimalFirePoint(DataInitializer.initBattleGrid()));
            appendOutput(output);
        });

        return panel;
    }

    // ========== 华容道迷宫面板 ==========
    private JPanel createMazePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBorder(new TitledBorder("华容道迷宫"));

        JButton btnBFS = new JButton("BFS搜索路径");
        JButton btnAStar = new JButton("A*搜索（拓展）");

        btnPanel.add(btnBFS);
        btnPanel.add(btnAStar);

        JTextArea mazeArea = new JTextArea(10, 20);
        mazeArea.setEditable(false);
        mazeArea.setFont(new Font("宋体", Font.PLAIN, 14));
        mazeArea.setText("迷宫地图：\n🟢=曹操起点  🚩=出口\n🧱=障碍  ⬛=空地");

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(mazeArea), BorderLayout.CENTER);

        btnBFS.addActionListener(e -> {
            HuarongMaze maze = new HuarongMaze();
            String output = captureOutput(() -> maze.printPath(DataInitializer.initMaze()));
            appendOutput(output);
        });

        btnAStar.addActionListener(e -> {
            HuarongMaze maze = new HuarongMaze();
            String output = captureOutput(() -> maze.printPathAStar(DataInitializer.initMaze()));
            appendOutput(output);
        });

        return panel;
    }

    // ========== 拓展功能面板 ==========
    private JPanel createExtensionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(new TitledBorder("拓展功能"));

        // 拓展1：维吉尼亚密码
        JPanel vigenerePanel = new JPanel(new BorderLayout());
        vigenerePanel.setBorder(new TitledBorder("维吉尼亚密码（更强加密）"));
        JPanel vInput = new JPanel(new GridLayout(2, 2, 5, 5));
        vInput.add(new JLabel("密文："));
        JTextField vCipher = new JTextField();
        vInput.add(vCipher);
        vInput.add(new JLabel("关键词："));
        JTextField vKey = new JTextField("key");
        vInput.add(vKey);
        JButton vBtn = new JButton("解密");
        vigenerePanel.add(vInput, BorderLayout.CENTER);
        vigenerePanel.add(vBtn, BorderLayout.EAST);

        vBtn.addActionListener(e -> {
            VigenereCipher vc = new VigenereCipher();
            String result = vc.decrypt(vCipher.getText(), vKey.getText());
            appendOutput("维吉尼亚解密: " + result);
        });

        // 拓展2：地形路径
        JPanel terrainPanel = new JPanel(new BorderLayout());
        terrainPanel.setBorder(new TitledBorder("带地形速度的最短时间路径"));
        JButton tBtn = new JButton("运行地形路径分析");
        terrainPanel.add(tBtn, BorderLayout.CENTER);

        tBtn.addActionListener(e -> {
            TerrainPathFinder finder = TerrainPathFinder.buildDefaultMap();
            String output = captureOutput(() -> finder.runDefault());
            appendOutput(output);
        });

        // 拓展3：动态草船借箭
        JPanel dynamicPanel = new JPanel(new BorderLayout());
        dynamicPanel.setBorder(new TitledBorder("动态草船借箭"));
        JButton dBtn = new JButton("运行动态模拟");
        dynamicPanel.add(dBtn, BorderLayout.CENTER);

        dBtn.addActionListener(e -> {
            GrassBoatSimulation boat = new GrassBoatSimulation();
            String output = captureOutput(() -> boat.dynamicSimulate(DataInitializer.initScarecrows(), 8));
            appendOutput(output);
        });

        panel.add(vigenerePanel);
        panel.add(terrainPanel);
        panel.add(dynamicPanel);

        return panel;
    }

    /**
     * 启动GUI - 直接显示
     */
    public void launch() {
        setVisible(true);
    }
}
