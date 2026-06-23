package sunwu.service;

/**
 * 密文解密摘要，保留原文和解密后的明文，便于展示对照。
 */
public record CipherSummary(
    String encoded,
    String decoded
) {
}
