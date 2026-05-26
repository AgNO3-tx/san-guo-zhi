package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.DepartmentType;
import sunwu.domain.General;
import sunwu.service.HierarchyService;
import sunwu.service.HierarchyView;

import java.util.Set;
import java.util.stream.Collectors;

public final class HierarchyServiceTest {
    private HierarchyServiceTest() {
    }

    public static void run() {
        HierarchyService service = new HierarchyService();
        HierarchyView view = service.buildHierarchy(SampleData.generals());

        TestSupport.assertEquals("Sun Quan", view.root().value().name(), "Root should be Sun Quan.");
        TestSupport.assertEquals(2, view.root().children().size(), "Emperor should have two chiefs.");
        TestSupport.assertTrue(view.departmentMembers().containsKey(DepartmentType.MILITARY), "Military department missing.");
        TestSupport.assertTrue(view.departmentMembers().containsKey(DepartmentType.MANAGEMENT), "Management department missing.");

        Set<String> assigned = view.departmentMembers().values().stream()
            .flatMap(Set::stream)
            .map(General::name)
            .collect(Collectors.toSet());
        TestSupport.assertEquals(10, assigned.size(), "All generals should be assigned exactly once.");
        TestSupport.assertTrue(
            view.departmentMembers().get(DepartmentType.MILITARY).stream().anyMatch(g -> g.name().equals("Gan Ning")),
            "Gan Ning should be assigned to the military department."
        );
        TestSupport.assertTrue(
            view.departmentMembers().get(DepartmentType.MANAGEMENT).stream().anyMatch(g -> g.name().equals("Zhu Ge Jin")),
            "Zhu Ge Jin should be assigned to the management department."
        );
    }
}
