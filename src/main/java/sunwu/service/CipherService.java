package sunwu.service;

/**
 * 加密解密服务。
 * 覆盖 Caesar 解密、PDF 中的特殊符号语法，以及扩展的 &num{} 加密规则。
 */
public final class CipherService {
    /**
     * 先执行 Caesar 反向移位，再解析 ^、$、() 特殊语法。
     */
    public CipherSummary decryptClassic(String encrypted, int shift) {
        StringBuilder shifted = new StringBuilder();
        for (int index = 0; index < encrypted.length(); index++) {
            char current = encrypted.charAt(index);
            if (current >= 'a' && current <= 'z') {
                // 只处理小写英文字母；标点、特殊符号保留给第二阶段解析。
                int value = current - 'a';
                int decoded = (value - shift + 26) % 26;
                shifted.append((char) ('a' + decoded));
            } else {
                shifted.append(current);
            }
        }

        String decoded = applySyntax(shifted.toString());
        return new CipherSummary(encrypted, decoded);
    }

    /**
     * 扩展加密：每个字符增加 4 + subtractRule，并用 &num{} 包起来。
     */
    public String encryptSecure(String input, int subtractRule) {
        StringBuilder builder = new StringBuilder();
        builder.append("&").append(subtractRule).append("{");
        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            builder.append((char) (current + 4 + subtractRule));
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * 扩展解密：读出 &num{} 中的 num，再还原 payload。
     */
    public String decryptSecure(String input) {
        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        int subtractRule = Integer.parseInt(input.substring(1, start));
        String payload = input.substring(start + 1, end);
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < payload.length(); index++) {
            builder.append((char) (payload.charAt(index) - 4 - subtractRule));
        }
        return builder.toString();
    }

    /**
     * 解析特殊语法：^ 表示下一字符大写，$ 表示空格，括号内容反转。
     */
    private String applySyntax(String shiftedText) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < shiftedText.length(); index++) {
            char current = shiftedText.charAt(index);
            if (current == '^' && index + 1 < shiftedText.length()) {
                builder.append(Character.toUpperCase(shiftedText.charAt(++index)));
            } else if (current == '$') {
                builder.append(' ');
            } else if (current == '(') {
                int closing = shiftedText.indexOf(')', index);
                // 当前样例只需要处理成对括号；括号内文字反转后拼入结果。
                String reversed = new StringBuilder(shiftedText.substring(index + 1, closing)).reverse().toString();
                builder.append(reversed);
                index = closing;
            } else {
                builder.append(current);
            }
        }
        return builder.toString();
    }
}
