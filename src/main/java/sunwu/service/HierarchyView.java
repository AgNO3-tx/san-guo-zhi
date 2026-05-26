package sunwu.service;

import sunwu.domain.DepartmentType;
import sunwu.domain.General;
import sunwu.domain.HierarchyNode;

import java.util.Map;
import java.util.Set;

public record HierarchyView(
    HierarchyNode root,
    Map<DepartmentType, Set<General>> departmentMembers
) {
}
