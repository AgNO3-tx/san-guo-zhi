package member4_battlefield;

import java.util.*;

/**
 * 华容道迷宫追击
 * 2D迷宫：2=起点，3=终点
 * 搜索曹操逃亡路径并输出
 * 包含基础BFS和拓展A*算法
 */
public class HuarongMaze {

    private int rows, cols;
    private int[][] maze;
    private boolean[][] visited;
    private int[][] prev;

    // 4连通方向：上、下、左、右
    private int[] dr = {-1, 1, 0, 0};
    private int[] dc = {0, 0, -1, 1};
    private String[] dirNames = {"↑上", "↓下", "←左", "→右"};

    public HuarongMaze() {
    }

    /**
     * BFS搜索迷宫路径
     * @param maze 迷宫矩阵（2=起点，3=终点，0=空地，1=障碍）
     * @return 路径坐标列表
     */
    public List<int[]> findPath(int[][] maze) {
        this.maze = maze;
        this.rows = maze.length;
        this.cols = maze[0].length;
        this.visited = new boolean[rows][cols];
        this.prev = new int[rows][cols];
        for (int[] row : prev) Arrays.fill(row, -1);

        // 找到起点和终点
        int startR = -1, startC = -1, endR = -1, endC = -1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (maze[r][c] == 2) { startR = r; startC = c; }
                if (maze[r][c] == 3) { endR = r; endC = c; }
            }
        }

        if (startR == -1 || endR == -1) {
            System.out.println("⚠ 迷宫缺少起点(2)或终点(3)！");
            return new ArrayList<>();
        }

        // BFS
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0], c = current[1];

            if (r == endR && c == endC) {
                // 重建路径
                return reconstructPath(startR, startC, endR, endC);
            }

            for (int i = 0; i < 4; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];
                if (isValid(nr, nc) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    prev[nr][nc] = i; // 记录方向
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        return new ArrayList<>(); // 无路径
    }

    /**
     * A*算法搜索迷宫路径（拓展功能）
     * 使用曼哈顿距离作为启发函数
     */
    public List<int[]> findPathAStar(int[][] maze) {
        this.maze = maze;
        this.rows = maze.length;
        this.cols = maze[0].length;

        // 找到起点和终点
        int startR = -1, startC = -1, endR = -1, endC = -1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (maze[r][c] == 2) { startR = r; startC = c; }
                if (maze[r][c] == 3) { endR = r; endC = c; }
            }
        }

        // A*算法
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        Map<String, Integer> gScore = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();

        String startKey = startR + "," + startC;
        gScore.put(startKey, 0);
        openSet.add(new Node(startR, startC, 0, heuristic(startR, startC, endR, endC)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = current.r + "," + current.c;

            if (current.r == endR && current.c == endC) {
                return reconstructPathAStar(cameFrom, currentKey, startKey);
            }

            if (closedSet.contains(currentKey)) continue;
            closedSet.add(currentKey);

            for (int i = 0; i < 4; i++) {
                int nr = current.r + dr[i];
                int nc = current.c + dc[i];
                if (!isValid(nr, nc)) continue;

                String neighborKey = nr + "," + nc;
                int tentativeG = gScore.get(currentKey) + 1;

                if (!gScore.containsKey(neighborKey) || tentativeG < gScore.get(neighborKey)) {
                    gScore.put(neighborKey, tentativeG);
                    cameFrom.put(neighborKey, currentKey);
                    openSet.add(new Node(nr, nc, tentativeG, heuristic(nr, nc, endR, endC)));
                }
            }
        }

        return new ArrayList<>();
    }

    static class Node implements Comparable<Node> {
        int r, c;
        int g, f;

        Node(int r, int c, int g, int h) {
            this.r = r;
            this.c = c;
            this.g = g;
            this.f = g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    private int heuristic(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols && maze[r][c] != 1;
    }

    private List<int[]> reconstructPath(int startR, int startC, int endR, int endC) {
        List<int[]> path = new ArrayList<>();
        int r = endR, c = endC;

        while (!(r == startR && c == startC)) {
            path.add(0, new int[]{r, c});
            int dir = prev[r][c];
            r -= dr[dir];
            c -= dc[dir];
        }
        path.add(0, new int[]{startR, startC});
        return path;
    }

    private List<int[]> reconstructPathAStar(Map<String, String> cameFrom, String current, String start) {
        List<int[]> path = new ArrayList<>();
        String key = current;

        while (!key.equals(start)) {
            String[] parts = key.split(",");
            path.add(0, new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
            key = cameFrom.get(key);
        }
        String[] parts = start.split(",");
        path.add(0, new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        return path;
    }

    /**
     * 打印迷宫和路径
     */
    public void printPath(int[][] maze) {
        List<int[]> path = findPath(maze);

        System.out.println("\n🏃 华容道迷宫追击");
        System.out.println("=".repeat(60));
        System.out.println("迷宫地图（2=曹操起点，3=出口，1=障碍，0=空地）：");
        printMaze(maze, path);

        if (path.isEmpty()) {
            System.out.println("\n⚠ 曹操无路可逃！");
        } else {
            System.out.printf("\n✅ 曹操逃亡路径（共%d步）：\n", path.size() - 1);
            for (int i = 0; i < path.size(); i++) {
                int[] p = path.get(i);
                System.out.printf("  步骤%d: (%d, %d)", i, p[0], p[1]);
                if (maze[p[0]][p[1]] == 2) System.out.print(" ←起点");
                if (maze[p[0]][p[1]] == 3) System.out.print(" ←出口");
                System.out.println();
            }
        }
        System.out.println("=".repeat(60));
    }

    /**
     * A*算法打印
     */
    public void printPathAStar(int[][] maze) {
        List<int[]> path = findPathAStar(maze);

        System.out.println("\n⭐ A*算法搜索华容道（拓展功能）");
        System.out.println("=".repeat(60));

        if (path.isEmpty()) {
            System.out.println("\n⚠ 曹操无路可逃！");
        } else {
            System.out.printf("\n✅ A*找到路径（共%d步）：\n", path.size() - 1);
            printMaze(maze, path);
            for (int i = 0; i < path.size(); i++) {
                int[] p = path.get(i);
                System.out.printf("  步骤%d: (%d, %d)%s\n", i, p[0], p[1],
                        maze[p[0]][p[1]] == 3 ? " ←出口" : "");
            }
        }
        System.out.println("=".repeat(60));
    }

    private void printMaze(int[][] maze, List<int[]> path) {
        Set<String> pathSet = new HashSet<>();
        if (path != null) {
            for (int[] p : path) {
                pathSet.add(p[0] + "," + p[1]);
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (maze[r][c] == 1) {
                    System.out.print("🧱"); // 障碍
                } else if (maze[r][c] == 2) {
                    System.out.print("🟢"); // 起点
                } else if (maze[r][c] == 3) {
                    System.out.print("🚩"); // 终点
                } else if (pathSet.contains(r + "," + c)) {
                    System.out.print("⬜"); // 路径
                } else {
                    System.out.print("⬛"); // 空地
                }
            }
            System.out.println();
        }
    }

    /**
     * 运行默认示例
     */
    public void runDefault() {
        int[][] maze = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        printPath(maze);
        printPathAStar(maze);
    }
}
