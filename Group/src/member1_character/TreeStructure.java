package member1_character;

import java.util.*;

/**
 * 吴国三级树形组织结构
 * 顶层：孙权（君主）
 * 中层：周瑜（军事）、张昭（内政）
 * 底层：所有武将
 */
public class TreeStructure {
    private TreeNode root; // 孙权

    public TreeStructure() {
        root = new TreeNode("孙权", "君主", 0);
    }

    /**
     * 构建三级树结构
     */
    public void buildTree(List<Character> characters) {
        // 创建中层节点
        TreeNode zhouYu = new TreeNode("周瑜", "军事部", 1);
        TreeNode zhangZhao = new TreeNode("张昭", "内政部", 1);

        root.addChild(zhouYu);
        root.addChild(zhangZhao);

        // 分配武将到对应部门
        for (Character c : characters) {
            TreeNode node = new TreeNode(c.getName(), c.getDepartment(), 2);
            node.setCharacter(c);
            if (c.getDepartment().equals("军事部")) {
                zhouYu.addChild(node);
            } else {
                zhangZhao.addChild(node);
            }
        }
    }

    /**
     * 打印组织结构图
     */
    public void printHierarchy() {
        System.out.println("=".repeat(60));
        System.out.println("        吴国三级树形组织结构");
        System.out.println("=".repeat(60));
        printNode(root, 0);
        System.out.println("=".repeat(60));
    }

    private void printNode(TreeNode node, int level) {
        String indent = "  ".repeat(level);
        if (level == 0) {
            System.out.println(indent + "👑 " + node.getName() + " [" + node.getTitle() + "]");
        } else if (level == 1) {
            System.out.println(indent + "├─ " + node.getName() + " [" + node.getTitle() + "]");
        } else {
            System.out.println(indent + "│  ├─ " + node.getName());
        }
        for (TreeNode child : node.getChildren()) {
            printNode(child, level + 1);
        }
    }

    /**
     * 获取树结构字符串表示
     */
    public String getHierarchyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("        吴国三级树形组织结构\n");
        sb.append("=".repeat(60)).append("\n");
        buildString(root, 0, sb);
        sb.append("=".repeat(60)).append("\n");
        return sb.toString();
    }

    private void buildString(TreeNode node, int level, StringBuilder sb) {
        String indent = "  ".repeat(level);
        if (level == 0) {
            sb.append(indent).append("👑 ").append(node.getName()).append(" [").append(node.getTitle()).append("]\n");
        } else if (level == 1) {
            sb.append(indent).append("├─ ").append(node.getName()).append(" [").append(node.getTitle()).append("]\n");
        } else {
            sb.append(indent).append("│  ├─ ").append(node.getName()).append("\n");
        }
        for (TreeNode child : node.getChildren()) {
            buildString(child, level + 1, sb);
        }
    }

    /**
     * 统计各部门人数
     */
    public void printDepartmentStats() {
        int militaryCount = 0, civilCount = 0;
        for (TreeNode child : root.getChildren()) {
            if (child.getTitle().equals("军事部")) {
                militaryCount = child.getChildren().size();
            } else {
                civilCount = child.getChildren().size();
            }
        }
        System.out.println("\n📊 部门统计：");
        System.out.println("  军事部：" + militaryCount + " 人");
        System.out.println("  内政部：" + civilCount + " 人");
        System.out.println("  总计：" + (militaryCount + civilCount) + " 人");
    }

    public TreeNode getRoot() { return root; }
}
