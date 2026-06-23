package sunwu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 权力层级树节点。
 * 每个节点保存一个武将，并维护只允许通过 addChild 修改的子节点列表。
 */
public final class HierarchyNode {
    private final General value;
    private final List<HierarchyNode> children = new ArrayList<>();

    public HierarchyNode(General value) {
        this.value = value;
    }

    public General value() {
        return value;
    }

    public List<HierarchyNode> children() {
        // 返回不可变视图，防止调用方绕过树节点的封装直接改 children。
        return Collections.unmodifiableList(children);
    }

    public void addChild(HierarchyNode child) {
        children.add(child);
    }
}
