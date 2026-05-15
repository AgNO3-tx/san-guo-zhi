package member1_character;

import java.util.*;

/**
 * 二分查找 - 按指定属性搜索武将
 */
public class BinarySearch {

    /**
     * 二分查找指定属性值的武将
     * @param characters 已按属性排序的列表（降序）
     * @param attribute 属性名
     * @param target 目标值
     * @return 匹配的武将，未找到返回null
     */
    public Character search(List<Character> characters, String attribute, int target) {
        int left = 0, right = characters.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = characters.get(mid).getAttribute(attribute);
            if (midVal == target) {
                return characters.get(mid);
            } else if (midVal > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    /**
     * 查找所有匹配指定属性值的武将（可能有多个相同值）
     */
    public List<Character> searchAll(List<Character> characters, String attribute, int target) {
        List<Character> result = new ArrayList<>();
        // 先找到任意一个匹配
        int left = 0, right = characters.size() - 1;
        int foundIdx = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = characters.get(mid).getAttribute(attribute);
            if (midVal == target) {
                foundIdx = mid;
                break;
            } else if (midVal > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        if (foundIdx == -1) return result;

        // 向左右扩展查找相同值
        int i = foundIdx;
        while (i >= 0 && characters.get(i).getAttribute(attribute) == target) {
            result.add(characters.get(i));
            i--;
        }
        i = foundIdx + 1;
        while (i < characters.size() && characters.get(i).getAttribute(attribute) == target) {
            result.add(characters.get(i));
            i++;
        }
        return result;
    }

    /**
     * 演示二分查找
     */
    public void demoSearch(List<Character> characters) {
        CharacterSorter sorter = new CharacterSorter();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n🔍 二分查找武将");
        System.out.println("可选属性：领导力、武力、智力、政治、生命值");
        System.out.print("请输入要查找的属性：");
        String attr = scanner.nextLine().trim();
        System.out.print("请输入要查找的值：");
        int value = scanner.nextInt();
        scanner.nextLine(); // 消耗换行

        // 先按属性排序
        List<Character> sorted = sorter.sortByAttribute(characters, attr);

        Character found = search(sorted, attr, value);
        if (found != null) {
            System.out.println("\n✅ 找到武将：" + found);
            // 显示附近武将
            int idx = sorted.indexOf(found);
            System.out.println("\n附近武将：");
            for (int i = Math.max(0, idx - 2); i <= Math.min(sorted.size() - 1, idx + 2); i++) {
                System.out.println("  " + sorted.get(i));
            }
        } else {
            System.out.println("\n❌ 未找到 " + attr + " = " + value + " 的武将");
        }
    }
}
