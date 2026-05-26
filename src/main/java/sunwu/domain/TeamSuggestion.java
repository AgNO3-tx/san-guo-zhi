package sunwu.domain;

import java.util.List;

public record TeamSuggestion(
    AbilityType focus,
    List<General> members,
    int totalAbility,
    TeamGrade grade
) {
}
