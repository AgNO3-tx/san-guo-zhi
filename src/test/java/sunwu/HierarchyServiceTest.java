package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.ArmyType;
import sunwu.domain.DepartmentType;
import sunwu.domain.General;
import sunwu.service.HierarchyService;
import sunwu.service.HierarchyView;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 验证吴国三层层级树和部门自动分配规则。
 */
public final class HierarchyServiceTest {
    private HierarchyServiceTest() {
    }

    public static void run() {
        HierarchyService service = new HierarchyService();
        HierarchyView view = service.buildHierarchy(SampleData.generals());

        // 根节点和两位 chief 是层级树的基本结构。
        TestSupport.assertEquals("Sun Quan", view.root().value().name(), "Root should be Sun Quan.");
        TestSupport.assertEquals(2, view.root().children().size(), "Emperor should have two chiefs.");
        TestSupport.assertTrue(view.departmentMembers().containsKey(DepartmentType.MILITARY), "Military department missing.");
        TestSupport.assertTrue(view.departmentMembers().containsKey(DepartmentType.MANAGEMENT), "Management department missing.");

        // 所有普通武将都必须被分配且只分配一次。
        Set<String> assigned = view.departmentMembers().values().stream()
            .flatMap(Set::stream)
            .map(General::name)
            .collect(Collectors.toSet());
        TestSupport.assertEquals(10, assigned.size(), "All generals should be assigned exactly once.");
        // 选取军事和管理各一个代表样例，验证自动分配方向。
        TestSupport.assertTrue(
            view.departmentMembers().get(DepartmentType.MILITARY).stream().anyMatch(g -> g.name().equals("Gan Ning")),
            "Gan Ning should be assigned to the military department."
        );
        TestSupport.assertTrue(
            view.departmentMembers().get(DepartmentType.MANAGEMENT).stream().anyMatch(g -> g.name().equals("Zhu Ge Jin")),
            "Zhu Ge Jin should be assigned to the management department."
        );

        General intelligenceSpecialist = new General("Rule Check Strategist", ArmyType.ARCHER, 60, 100, 61, 0, 50);
        TestSupport.assertEquals(
            DepartmentType.MANAGEMENT,
            service.assignDepartment(intelligenceSpecialist),
            "Department assignment should follow the PDF rule: intelligence greater than strength means management."
        );
    }
}
