package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.service.FoodHarvestService;
import sunwu.service.FoodSimulationResult;

import java.util.Set;

public final class FoodHarvestServiceTest {
    private FoodHarvestServiceTest() {
    }

    public static void run() {
        FoodHarvestService service = new FoodHarvestService();

        FoodSimulationResult route = service.planFoodHarvest(SampleData.battlefieldGraph(), Set.of(9));
        TestSupport.assertTrue(!route.path().contains(9), "Path should exclude node 9 when it has no food.");
        TestSupport.assertEquals(1, route.path().getFirst(), "Food route should start from node 1.");
        TestSupport.assertEquals(1, route.path().getLast(), "Food route should end at node 1.");

        FoodSimulationResult production = service.maximizeFoodProduction(SampleData.generals(), AbilityType.POLITIC, 8);
        TestSupport.assertEquals(3, production.assignedTeam().size(), "Food production team should have 3 generals.");
        TestSupport.assertTrue(production.totalFood() >= 800, "Food production should include team buff.");

        FoodSimulationResult bestSimulation = service.planGuardedCampSimulation(
            SampleData.weightedBattlefieldGraph(),
            SampleData.generals()
        );
        TestSupport.assertTrue(bestSimulation.totalCost() > 0, "Guarded camp simulation should have positive cost.");
        TestSupport.assertEquals(3, bestSimulation.generalRoutes().size(), "Simulation should assign 3 generals.");
    }
}
