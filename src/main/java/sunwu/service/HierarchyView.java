package sunwu.service;

import sunwu.domain.DepartmentType;
import sunwu.domain.General;
import sunwu.domain.HierarchyNode;

import java.util.Map;
import java.util.Set;

/**
 * 层级树展示模型，包含树根和按部门归类的成员。
 */
public record HierarchyView(
    HierarchyNode root,
    Map<DepartmentType, Set<General>> departmentMembers
) {
}
