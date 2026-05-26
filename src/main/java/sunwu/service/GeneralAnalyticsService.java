package sunwu.service;

import sunwu.domain.AbilityType;
import sunwu.domain.General;
import sunwu.domain.TeamGrade;
import sunwu.domain.TeamSuggestion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GeneralAnalyticsService {
    public List<String> sortGeneralNames(List<General> generals, AbilityType abilityType) {
        return generals.stream()
            .sorted(Comparator.comparingInt((General general) -> general.ability(abilityType)).reversed()
                .thenComparing(General::name))
            .map(General::name)
            .toList();
    }

    public List<String> searchByAbility(List<General> generals, AbilityType abilityType, int targetValue) {
        List<General> sorted = generals.stream()
            .sorted(Comparator.comparingInt(general -> general.ability(abilityType)))
            .toList();
        int found = binarySearch(sorted, abilityType, targetValue);
        if (found < 0) {
            return List.of();
        }

        int start = found;
        while (start > 0 && sorted.get(start - 1).ability(abilityType) == targetValue) {
            start--;
        }
        int end = found;
        while (end + 1 < sorted.size() && sorted.get(end + 1).ability(abilityType) == targetValue) {
            end++;
        }

        List<String> names = new ArrayList<>();
        for (int index = start; index <= end; index++) {
            names.add(sorted.get(index).name());
        }
        names.sort(String::compareTo);
        return names;
    }

    public TeamSuggestion bestTeamForAbility(List<General> generals, AbilityType abilityType) {
        List<General> sorted = generals.stream()
            .sorted(Comparator.comparingInt((General general) -> general.ability(abilityType)).reversed())
            .limit(3)
            .toList();
        int total = sorted.stream().mapToInt(general -> general.ability(abilityType)).sum();
        return new TeamSuggestion(abilityType, new ArrayList<>(sorted), total, toGrade(total));
    }

    public List<TeamSuggestion> rankTeamsForAbility(List<General> generals, AbilityType abilityType) {
        List<TeamSuggestion> teams = new ArrayList<>();
        for (int first = 0; first < generals.size(); first++) {
            for (int second = first + 1; second < generals.size(); second++) {
                for (int third = second + 1; third < generals.size(); third++) {
                    List<General> members = List.of(generals.get(first), generals.get(second), generals.get(third));
                    int total = members.stream().mapToInt(general -> general.ability(abilityType)).sum();
                    teams.add(new TeamSuggestion(abilityType, members, total, toGrade(total)));
                }
            }
        }
        teams.sort(Comparator
            .comparingInt(TeamSuggestion::totalAbility)
            .reversed()
            .thenComparing(team -> team.members().stream().map(General::name).toList().toString()));
        return teams;
    }

    public TeamGrade toGrade(int total) {
        if (total >= 250) {
            return TeamGrade.S;
        }
        if (total >= 220) {
            return TeamGrade.A;
        }
        if (total >= 190) {
            return TeamGrade.B;
        }
        return TeamGrade.C;
    }

    private int binarySearch(List<General> sorted, AbilityType abilityType, int targetValue) {
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int value = sorted.get(middle).ability(abilityType);
            if (value == targetValue) {
                return middle;
            }
            if (value < targetValue) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }
}
