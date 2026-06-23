package sunwu.domain;

/**
 * 武将基础数据。
 * record 让这些样例数据保持不可变，方便在多个算法服务之间共享。
 */
public record General(
    String name,
    ArmyType armyType,
    int strength,
    int leadership,
    int intelligence,
    int politic,
    int hitPoint
) {
    /**
     * 根据能力枚举统一取值，避免排序、查找和组队逻辑重复写 switch。
     */
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
