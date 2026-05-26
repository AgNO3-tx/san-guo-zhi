package sunwu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return Collections.unmodifiableList(children);
    }

    public void addChild(HierarchyNode child) {
        children.add(child);
    }
}
