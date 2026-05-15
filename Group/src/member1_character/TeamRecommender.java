package member1_character;

import java.util.*;

/**
 * 分级组队推荐 - 按S/A/B/C分级，为各属性推荐3人组队
 */
public class TeamRecommender {

    /**
     * 按能力总分分级
     */
    public Map<String, List<Character>> gradeCharacters(List<Character> characters) {
        Map<String, List<Character>> gradeMap = new LinkedHashMap<>();
        gradeMap.put("S", new ArrayList<>());
        gradeMap.put("A", new ArrayList<>());
        gradeMap.put("B", new ArrayList<>());
        gradeMap.put("C", new ArrayList<>());

        for (Character c : characters) {
            gradeMap.get(c.getGrade()).add(c);
        }
        return gradeMap;
    }

    /**
     * 为指定属性推荐3人组队（取该属性最高的3人）
     */
    public List<Character> recommendTeam(List<Character> characters, String attribute) {
        CharacterSorter sorter = new CharacterSorter();
        List<Character> sorted = sorter.sortByAttribute(characters, attribute);
        return sorted.subList(0, Math.min(3, sorted.size()));
    }

    /**
     * 打印分级结果
     */
    public void printGrades(List<Character> characters) {
        Map<String, List<Character>> gradeMap = gradeCharacters(characters);
        System.out.println("\n📊 武将能力分级（按总分）：");
        System.out.println("=".repeat(70));
        for (String grade : new String[]{"S", "A", "B", "C"}) {
            List<Character> list = gradeMap.get(grade);
            System.out.printf("【%s级】%d人：", grade, list.size());
            for (Character c : list) {
                System.out.printf("%s(%d) ", c.getName(), c.getTotalScore());
            }
            System.out.println();
        }
        System.out.println("=".repeat(70));
    }

    /**
     * 打印各属性推荐队伍
     */
    public void printTeamRecommendations(List<Character> characters) {
        String[] attrs = {"政治", "领导力", "武力", "智力"};
        String[] descs = {"政治人才", "领导人才", "武力人才", "智谋人才"};

        System.out.println("\n🏆 各属性3人组队推荐：");
        System.out.println("=".repeat(60));
        for (int i = 0; i < attrs.length; i++) {
            List<Character> team = recommendTeam(characters, attrs[i]);
            System.out.printf("【%s】%s：", descs[i], attrs[i]);
            for (Character c : team) {
                System.out.printf("%s(%d) ", c.getName(), c.getAttribute(attrs[i]));
            }
            System.out.println();
        }
        System.out.println("=".repeat(60));
    }
}
