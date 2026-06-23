package sunwu.service;

import sunwu.domain.ArmyType;
import sunwu.domain.DepartmentType;
import sunwu.domain.General;
import sunwu.domain.HierarchyNode;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 吴国层级树服务。
 * 根据武将能力自动分配部门，并构建 Sun Quan -> 两位 chief -> 普通武将的三层结构。
 */
public final class HierarchyService {
    /**
     * 构建完整层级视图，同时保留每个部门的成员集合。
     */
    public HierarchyView buildHierarchy(List<General> generals) {
        General emperor = new General("Sun Quan", ArmyType.CAVALRY, 96, 98, 72, 77, 95);
        General chiefMilitary = findGeneral(generals, "Zhou Yu");
        General chiefManagement = findGeneral(generals, "Zhang Zhao");

        HierarchyNode root = new HierarchyNode(emperor);
        HierarchyNode military = new HierarchyNode(chiefMilitary);
        HierarchyNode management = new HierarchyNode(chiefManagement);
        root.addChild(military);
        root.addChild(management);

        Map<DepartmentType, Set<General>> departmentMembers = new EnumMap<>(DepartmentType.class);
        departmentMembers.put(DepartmentType.MILITARY, new LinkedHashSet<>());
        departmentMembers.put(DepartmentType.MANAGEMENT, new LinkedHashSet<>());

        // 为了输出稳定，先按姓名排序再挂到部门节点下。
        for (General general : generals.stream().sorted(Comparator.comparing(General::name)).toList()) {
            if (general.name().equals("Zhou Yu") || general.name().equals("Zhang Zhao")) {
                // 两位 chief 已经作为第二层节点，避免重复加入第三层。
                continue;
            }
            DepartmentType departmentType = assignDepartment(general);
            departmentMembers.get(departmentType).add(general);
            if (departmentType == DepartmentType.MILITARY) {
                military.addChild(new HierarchyNode(general));
            } else {
                management.addChild(new HierarchyNode(general));
            }
        }

        return new HierarchyView(root, departmentMembers);
    }

    /**
     * 部门自动分配规则来自 PDF：智力高于武力进管理部，否则进军事部。
     */
    public DepartmentType assignDepartment(General general) {
        return general.intelligence() > general.strength() ? DepartmentType.MANAGEMENT : DepartmentType.MILITARY;
    }

    /**
     * 层级树需要固定 chief，如果样例数据缺失则直接暴露配置错误。
     */
    private General findGeneral(List<General> generals, String name) {
        return generals.stream()
            .filter(general -> general.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Missing general: " + name));
    }
}
