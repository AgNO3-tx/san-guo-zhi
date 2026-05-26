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

public final class HierarchyService {
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

        for (General general : generals.stream().sorted(Comparator.comparing(General::name)).toList()) {
            if (general.name().equals("Zhou Yu") || general.name().equals("Zhang Zhao")) {
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

    public DepartmentType assignDepartment(General general) {
        int militaryScore = general.leadership() + general.strength();
        int managementScore = general.intelligence() + general.politic();
        return militaryScore > managementScore ? DepartmentType.MILITARY : DepartmentType.MANAGEMENT;
    }

    private General findGeneral(List<General> generals, String name) {
        return generals.stream()
            .filter(general -> general.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Missing general: " + name));
    }
}
