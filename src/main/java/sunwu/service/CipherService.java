package sunwu.service;

public final class CipherService {
    public CipherSummary decryptClassic(String encrypted, int shift) {
        StringBuilder shifted = new StringBuilder();
        for (int index = 0; index < encrypted.length(); index++) {
            char current = encrypted.charAt(index);
            if (current >= 'a' && current <= 'z') {
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
