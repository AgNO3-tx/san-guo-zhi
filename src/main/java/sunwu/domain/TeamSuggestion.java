package sunwu.domain;

import java.util.List;

/**
 * 针对某项能力推荐出来的一支三人队伍。
 */
public record TeamSuggestion(
    AbilityType focus,
    List<General> members,
    int totalAbility,
    TeamGrade grade
) {
}
