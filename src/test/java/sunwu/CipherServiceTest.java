package sunwu;

import sunwu.feature.encryption.CipherService;
import sunwu.feature.encryption.CipherSummary;

/**
 * 验证 Caesar 特殊语法解密和增强加密的往返能力。
 */
public final class CipherServiceTest {
    private CipherServiceTest() {
    }

    public static void run() {
        CipherService service = new CipherService();
        // 使用 PDF 中的完整密文样例，确保 ^、$、() 组合能正确解析。
        CipherSummary classic = service.decryptClassic(
            "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.",
            7
        );
        TestSupport.assertEquals(
            "Advise Cao Cao to use The Chain Strategem, which is to chain his battleships with strong iron chains.",
            classic.decoded(),
            "Classic Caesar decryption should match assignment sample."
        );

        // 增强加密不要求固定密文，但必须能完整解回原文。
        String secureEncrypted = service.encryptSecure("Attack at dawn", 3);
        String secureDecoded = service.decryptSecure(secureEncrypted);
        TestSupport.assertEquals("Attack at dawn", secureDecoded, "Secure cipher should round-trip correctly.");
    }
}
