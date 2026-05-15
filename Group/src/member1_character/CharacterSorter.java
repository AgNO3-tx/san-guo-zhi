package member1_character;

import java.util.*;

/**
 * 武将排序器 - 按领导力、武力、智力、政治、生命值排序
 */
public class CharacterSorter {

    /**
     * 按指定属性排序（降序）
     * @param characters 武将列表
     * @param attribute 属性名：领导力/武力/智力/政治/生命值
     * @return 排序后的新列表
     */
    public List<Character> sortByAttribute(List<Character> characters, String attribute) {
        List<Character> sorted = new ArrayList<>(characters);
        sorted.sort((a, b) -> b.getAttribute(attribute) - a.getAttribute(attribute));
        return sorted;
    }

    /**
     * 按总分排序（降序）
     */
    public List<Character> sortByTotalScore(List<Character> characters) {
        List<Character> sorted = new ArrayList<>(characters);
        sorted.sort((a, b) -> b.getTotalScore() - a.getTotalScore());
        return sorted;
    }

    /**
     * 打印排序结果
     */
    public void printSorted(List<Character> characters, String attribute) {
        List<Character> sorted = sortByAttribute(characters, attribute);
        System.out.println("\n📊 按【" + attribute + "】排序结果：");
        System.out.println("-".repeat(70));
        System.out.printf("%-4s | %-6s | %s\n", "排名", "姓名", attribute);
        System.out.println("-".repeat(70));
        int rank = 1;
        for (Character c : sorted) {
            System.out.printf("%-4d | %-6s | %d\n", rank++, c.getName(), c.getAttribute(attribute));
        }
        System.out.println("-".repeat(70));
    }

    /**
     * 打印所有排序结果
     */
    public void printAllSorts(List<Character> characters) {
        String[] attrs = {"领导力", "武力", "智力", "政治", "生命值"};
        for (String attr : attrs) {
            printSorted(characters, attr);
        }
        System.out.println("\n📊 按【总分】排序结果：");
        List<Character> sorted = sortByTotalScore(characters);
        System.out.println("-".repeat(80));
        System.out.printf("%-4s | %-6s | %s\n", "排名", "姓名", "领导力 武力 智力 政治 生命  总分  等级");
        System.out.println("-".repeat(80));
        int rank = 1;
        for (Character c : sorted) {
            System.out.printf("%-4d | %s\n", rank++, c.toString());
        }
        System.out.println("-".repeat(80));
    }
}
