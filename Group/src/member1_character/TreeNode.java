package member1_character;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点类 - 用于构建吴国三级组织结构
 */
public class TreeNode {
    private String name;
    private String title;      // 职位/部门
    private int level;         // 0=君主, 1=中层, 2=底层武将
    private Character character; // 关联的武将对象（底层节点）
    private List<TreeNode> children;

    public TreeNode(String name, String title, int level) {
        this.name = name;
        this.title = title;
        this.level = level;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public String getName() { return name; }
    public String getTitle() { return title; }
    public int getLevel() { return level; }
    public Character getCharacter() { return character; }
    public void setCharacter(Character character) { this.character = character; }
    public List<TreeNode> getChildren() { return children; }
}
