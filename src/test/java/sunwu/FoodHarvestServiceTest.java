package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.service.FoodHarvestService;
import sunwu.service.FoodSimulationResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 验证粮草基础路径、产量增益和三将占营模拟。
 */
public final class FoodHarvestServiceTest {
    private FoodHarvestServiceTest() {
    }

    public static void run() {
        FoodHarvestService service = new FoodHarvestService();

        // 无粮节点应从采集路线中排除，但起点和终点必须仍然是 1。
        FoodSimulationResult route = service.planFoodHarvest(SampleData.battlefieldGraph(), Set.of(9));
        TestSupport.assertTrue(!route.path().contains(9), "Path should exclude node 9 when it has no food.");
        TestSupport.assertTrue(route.path().contains(3), "Path should still harvest food at node 3.");
        TestSupport.assertEquals(1, route.path().getFirst(), "Food route should start from node 1.");
        TestSupport.assertEquals(1, route.path().getLast(), "Food route should end at node 1.");
        FoodSimulationResult fullRoute = service.planFoodHarvest(SampleData.battlefieldGraph(), Set.of());
        for (int node = 2; node <= 10; node++) {
            TestSupport.assertTrue(fullRoute.path().contains(node), "Full food route should harvest node " + node + ".");
        }
        FoodSimulationResult connectorRoute = service.planFoodHarvest(SampleData.battlefieldGraph(), Set.of(8));
        TestSupport.assertTrue(connectorRoute.path().contains(8), "A no-food node should remain if it is needed to connect the route.");

        // 产量最大化必须选择三人队伍并应用至少基础产量的增益。
        FoodSimulationResult production = service.maximizeFoodProduction(SampleData.generals(), AbilityType.POLITIC, 8);
        TestSupport.assertEquals(3, production.assignedTeam().size(), "Food production team should have 3 generals.");
        TestSupport.assertTrue(production.totalFood() >= 800, "Food production should include team buff.");

        // 三将占营模拟需要给出正成本和三条武将路线。
        FoodSimulationResult bestSimulation = service.planGuardedCampSimulation(
            SampleData.weightedBattlefieldGraph(),
            SampleData.generals()
        );
        TestSupport.assertTrue(bestSimulation.totalCost() > 0, "Guarded camp simulation should have positive cost.");
        TestSupport.assertEquals(14, bestSimulation.totalCost(), "Guarded camp simulation should match the PDF sample minimum cost.");
        TestSupport.assertEquals(3, bestSimulation.generalRoutes().size(), "Simulation should assign 3 generals.");
        int summedCost = bestSimulation.generalRoutes().values().stream()
            .mapToInt(path -> Math.max(0, path.size() - 1))
            .sum();
        TestSupport.assertEquals(bestSimulation.totalCost(), summedCost, "Total cost should equal the sum of route step counts.");
        Set<Integer> coveredCamps = new HashSet<>();
        for (List<Integer> path : bestSimulation.generalRoutes().values()) {
            TestSupport.assertEquals(1, path.getFirst(), "Every guarded camp route should start from Node 1.");
            TestSupport.assertEquals(1, path.getLast(), "Every guarded camp route should return to Node 1.");
            Set<Integer> uniqueCamps = new HashSet<>(path);
            uniqueCamps.remove(1);
            coveredCamps.addAll(uniqueCamps);
        }
        for (int node = 2; node <= 10; node++) {
            TestSupport.assertTrue(coveredCamps.contains(node), "Guarded camp simulation should cover Node " + node + ".");
        }
    }
}
