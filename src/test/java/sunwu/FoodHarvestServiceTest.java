package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.service.FoodHarvestService;
import sunwu.service.FoodSimulationResult;

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
        TestSupport.assertEquals(1, route.path().getFirst(), "Food route should start from node 1.");
        TestSupport.assertEquals(1, route.path().getLast(), "Food route should end at node 1.");

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
        TestSupport.assertEquals(3, bestSimulation.generalRoutes().size(), "Simulation should assign 3 generals.");
    }
}
