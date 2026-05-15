package member2_strategy;

import java.util.*;

/**
 * 凯撒密码解密
 * 支持特殊符号：
 * ^ → 后面字母大写
 * $ → 空格
 * () → 括号内内容反转
 */
public class CaesarCipher {

    /**
     * 凯撒密码解密
     * @param ciphertext 密文
     * @param shift 偏移量（正数表示向后移位）
     * @return 解密后的明文
     */
    public String decrypt(String ciphertext, int shift) {
        // 第一步：凯撒移位解密
        StringBuilder shifted = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char decrypted = (char) ((c - base - shift + 26) % 26 + base);
                shifted.append(decrypted);
            } else {
                shifted.append(c);
            }
        }

        // 第二步：处理特殊符号
        return processSpecialSymbols(shifted.toString());
    }

    /**
     * 处理特殊符号
     * ^ → 后面字母大写
     * $ → 空格
     * () → 括号内内容反转
     */
    private String processSpecialSymbols(String text) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == '^') {
                // 后面字母大写
                i++;
                if (i < text.length()) {
                    result.append(Character.toUpperCase(text.charAt(i)));
                    i++;
                }
            } else if (c == '$') {
                // 空格
                result.append(' ');
                i++;
            } else if (c == '(') {
                // 括号内内容反转
                int start = i + 1;
                int end = text.indexOf(')', start);
                if (end != -1) {
                    String content = text.substring(start, end);
                    // 递归处理括号内的特殊符号
                    String processed = processSpecialSymbols(content);
                    result.append(new StringBuilder(processed).reverse());
                    i = end + 1;
                } else {
                    result.append(c);
                    i++;
                }
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }

    /**
     * 加密（用于测试验证）
     */
    public String encrypt(String plaintext, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char encrypted = (char) ((c - base + shift) % 26 + base);
                result.append(encrypted);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 演示凯撒密码解密
     */
    public void demoDecrypt() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n🔐 凯撒密码解密");
        System.out.println("=".repeat(60));
        System.out.println("特殊符号说明：");
        System.out.println("  ^x → 后面字母x大写");
        System.out.println("  $ → 空格");
        System.out.println("  (内容) → 括号内内容反转");
        System.out.println("=".repeat(60));

        System.out.print("请输入密文：");
        String ciphertext = scanner.nextLine().trim();
        System.out.print("请输入偏移量：");
        int shift = scanner.nextInt();
        scanner.nextLine();

        String plaintext = decrypt(ciphertext, shift);
        System.out.println("\n📝 解密结果：");
        System.out.println("  密文：" + ciphertext);
        System.out.println("  偏移：" + shift);
        System.out.println("  明文：" + plaintext);
    }

    /**
     * 运行预设示例
     */
    public void runExamples() {
        System.out.println("\n🔐 凯撒密码解密示例");
        System.out.println("=".repeat(60));

        // 示例1：基础凯撒
        String ex1 = decrypt("khoor", 3);
        System.out.println("示例1：khoor (偏移3) → " + ex1);

        // 示例2：带^符号
        String ex2 = decrypt("^h^e^l^l^o", 0);
        System.out.println("示例2：^h^e^l^l^o (偏移0) → " + ex2);

        // 示例3：带$符号
        String ex3 = decrypt("hello$world", 0);
        System.out.println("示例3：hello$world (偏移0) → " + ex3);

        // 示例4：带()符号
        String ex4 = decrypt("(olleh)", 0);
        System.out.println("示例4：(olleh) (偏移0) → " + ex4);

        // 示例5：综合
        String ex5 = decrypt("^(khoor)$(dlq)wkh$zruog", 3);
        System.out.println("示例5：^(khoor)$(dlq)wkh$zruog (偏移3) → " + ex5);
    }
}
