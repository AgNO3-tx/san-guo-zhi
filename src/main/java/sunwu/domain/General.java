package sunwu.domain;

public record General(
    String name,
    ArmyType armyType,
    int strength,
    int leadership,
    int intelligence,
    int politic,
    int hitPoint
) {
    public int ability(AbilityType abilityType) {
        return switch (abilityType) {
            case LEADERSHIP -> leadership;
            case STRENGTH -> strength;
            case INTELLIGENCE -> intelligence;
            case POLITIC -> politic;
            case HIT_POINT -> hitPoint;
        };
    }
}
