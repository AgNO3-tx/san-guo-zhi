package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.TeamGrade;
import sunwu.domain.TeamSuggestion;
import sunwu.service.GeneralAnalyticsService;

import java.util.List;

public final class GeneralAnalyticsServiceTest {
    private GeneralAnalyticsServiceTest() {
    }

    public static void run() {
        GeneralAnalyticsService service = new GeneralAnalyticsService();

        List<String> strengthSorted = service.sortGeneralNames(SampleData.generals(), AbilityType.STRENGTH);
        TestSupport.assertEquals("Gan Ning", strengthSorted.getFirst(), "Strongest general should be first.");
        TestSupport.assertEquals("Zhang Zhao", strengthSorted.getLast(), "Weakest general should be last.");

        List<String> match = service.searchByAbility(SampleData.generals(), AbilityType.LEADERSHIP, 98);
        TestSupport.assertTrue(match.contains("Huang Gai"), "Leadership search should find Huang Gai.");

        TeamSuggestion suggestion = service.bestTeamForAbility(SampleData.generals(), AbilityType.POLITIC);
        TestSupport.assertEquals(3, suggestion.members().size(), "Team suggestion should contain 3 members.");
        TestSupport.assertEquals(TeamGrade.S, suggestion.grade(), "Best political team should be S grade.");
    }
}
