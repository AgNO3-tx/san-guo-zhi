package member2_strategy;

import java.util.*;

/**
 * 维吉尼亚密码 - 替代凯撒密码的更强加密算法
 * 使用关键词进行多表替换，安全性远高于凯撒密码
 * 支持与凯撒密码相同的特殊符号处理（^ $ ()）
 */
public class VigenereCipher {

    /**
     * 维吉尼亚密码解密
     * @param ciphertext 密文
     * @param keyword 关键词
     * @return 解密后的明文
     */
    public String decrypt(String ciphertext, String keyword) {
        if (keyword.isEmpty()) return ciphertext;

        // 第一步：维吉尼亚解密
        StringBuilder shifted = new StringBuilder();
        int keyIndex = 0;
        keyword = keyword.toLowerCase();

        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = keyword.charAt(keyIndex % keyword.length()) - 'a';
                char decrypted = (char) ((c - base - shift + 26) % 26 + base);
                shifted.append(decrypted);
                keyIndex++;
            } else {
                shifted.append(c);
            }
        }

        // 第二步：处理特殊符号（复用凯撒密码的处理逻辑）
        CaesarCipher caesar = new CaesarCipher();
        // 使用反射调用私有方法，这里直接复制处理逻辑
        return processSpecialSymbols(shifted.toString());
    }

    /**
     * 维吉尼亚密码加密
     * @param plaintext 明文
     * @param keyword 关键词
     * @return 密文
     */
    public String encrypt(String plaintext, String keyword) {
        if (keyword.isEmpty()) return plaintext;

        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        keyword = keyword.toLowerCase();

        for (char c : plaintext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = keyword.charAt(keyIndex % keyword.length()) - 'a';
                char encrypted = (char) ((c - base + shift) % 26 + base);
                result.append(encrypted);
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 处理特殊符号（与CaesarCipher相同逻辑）
     */
    private String processSpecialSymbols(String text) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == '^') {
                i++;
                if (i < text.length()) {
                    result.append(Character.toUpperCase(text.charAt(i)));
                    i++;
                }
            } else if (c == '$') {
                result.append(' ');
                i++;
            } else if (c == '(') {
                int start = i + 1;
                int end = text.indexOf(')', start);
                if (end != -1) {
                    String content = text.substring(start, end);
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
     * 安全性分析
     */
    public void securityAnalysis() {
        System.out.println("\n🔒 维吉尼亚密码安全性分析");
        System.out.println("=".repeat(60));
        System.out.println("1. 多表替换：每个字母使用不同的移位值");
        System.out.println("2. 关键词长度未知，无法直接频率分析");
        System.out.println("3. 相比凯撒密码的优势：");
        System.out.println("   - 凯撒：固定偏移，26种可能，易暴力破解");
        System.out.println("   - 维吉尼亚：偏移随关键词变化，破解难度指数级上升");
        System.out.println("4. 破解方法：需先确定关键词长度（Kasiski测试）");
        System.out.println("   再对每组进行频率分析，复杂度远高于凯撒");
        System.out.println("5. 若关键词长度≥明文长度且随机，则为一次性密码本（绝对安全）");
        System.out.println("=".repeat(60));
    }

    /**
     * 演示维吉尼亚密码
     */
    public void demoVigenere() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n🔐 维吉尼亚密码（替代凯撒的更强加密）");
        System.out.println("=".repeat(60));
        System.out.print("请输入密文：");
        String ciphertext = scanner.nextLine().trim();
        System.out.print("请输入关键词：");
        String keyword = scanner.nextLine().trim();

        String plaintext = decrypt(ciphertext, keyword);
        System.out.println("\n📝 解密结果：");
        System.out.println("  密文：" + ciphertext);
        System.out.println("  关键词：" + keyword);
        System.out.println("  明文：" + plaintext);

        securityAnalysis();
    }

    /**
     * 运行预设示例
     */
    public void runExamples() {
        System.out.println("\n🔐 维吉尼亚密码示例");
        System.out.println("=".repeat(60));

        // 示例
        String key = "key";
        String plain = "helloworld";
        String encrypted = encrypt(plain, key);
        String decrypted = decrypt(encrypted, key);
        System.out.println("明文：" + plain);
        System.out.println("关键词：" + key);
        System.out.println("密文：" + encrypted);
        System.out.println("解密：" + decrypted);
    }
}
