package member1_character;

/**
 * 武将类 - 赤壁之战吴国武将
 * 属性：编号、姓名、领导力、武力、智力、政治、生命值、所属部门
 */
public class Character {
    private int id;
    private String name;
    private int leadership;    // 领导力
    private int force;         // 武力
    private int intelligence;  // 智力
    private int politics;      // 政治
    private int hp;            // 生命值
    private String department; // 部门：军事部 / 内政部

    public Character(int id, String name, int leadership, int force, int intelligence, int politics, int hp) {
        this.id = id;
        this.name = name;
        this.leadership = leadership;
        this.force = force;
        this.intelligence = intelligence;
        this.politics = politics;
        this.hp = hp;
        // 自动分配部门：智力 > 武力 → 内政部，武力 > 智力 → 军事部
        this.department = (intelligence > force) ? "内政部" : "军事部";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getLeadership() { return leadership; }
    public int getForce() { return force; }
    public int getIntelligence() { return intelligence; }
    public int getPolitics() { return politics; }
    public int getHp() { return hp; }
    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    /**
     * 获取能力总分
     */
    public int getTotalScore() {
        return leadership + force + intelligence + politics + hp;
    }

    /**
     * 获取能力等级
     * S ≥ 250, A ≥ 220, B ≥ 190, C < 190
     */
    public String getGrade() {
        int score = getTotalScore();
        if (score >= 250) return "S";
        if (score >= 220) return "A";
        if (score >= 190) return "B";
        return "C";
    }

    /**
     * 获取指定属性的值
     */
    public int getAttribute(String attr) {
        return switch (attr.toLowerCase()) {
            case "领导力", "leadership" -> leadership;
            case "武力", "force" -> force;
            case "智力", "intelligence" -> intelligence;
            case "政治", "politics" -> politics;
            case "生命值", "hp" -> hp;
            default -> 0;
        };
    }

    @Override
    public String toString() {
        return String.format("%-4s | 领导力:%-3d 武力:%-3d 智力:%-3d 政治:%-3d 生命:%-3d | 总分:%-3d %s | %s",
                name, leadership, force, intelligence, politics, hp, getTotalScore(), getGrade(), department);
    }
}
