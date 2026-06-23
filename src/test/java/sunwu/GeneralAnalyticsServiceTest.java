package sunwu;

import sunwu.data.SampleData;
import sunwu.domain.AbilityType;
import sunwu.domain.TeamGrade;
import sunwu.domain.TeamSuggestion;
import sunwu.service.GeneralAnalyticsService;

import java.util.List;

/**
 * 验证武将排序、能力二分查找和三人组队评级。
 */
public final class GeneralAnalyticsServiceTest {
    private GeneralAnalyticsServiceTest() {
    }

    public static void run() {
        GeneralAnalyticsService service = new GeneralAnalyticsService();

        // Strength 排序应把最强和最弱武将放在列表两端。
        List<String> strengthSorted = service.sortGeneralNames(SampleData.generals(), AbilityType.STRENGTH);
        TestSupport.assertEquals("Gan Ning", strengthSorted.getFirst(), "Strongest general should be first.");
        TestSupport.assertEquals("Zhang Zhao", strengthSorted.getLast(), "Weakest general should be last.");

        // Leadership=98 的样例武将应能通过二分查找命中。
        List<String> match = service.searchByAbility(SampleData.generals(), AbilityType.LEADERSHIP, 98);
        TestSupport.assertTrue(match.contains("Huang Gai"), "Leadership search should find Huang Gai.");

        // 政治能力最强三人队伍应达到 S 级。
        TeamSuggestion suggestion = service.bestTeamForAbility(SampleData.generals(), AbilityType.POLITIC);
        TestSupport.assertEquals(3, suggestion.members().size(), "Team suggestion should contain 3 members.");
        TestSupport.assertEquals(TeamGrade.S, suggestion.grade(), "Best political team should be S grade.");
    }
}
