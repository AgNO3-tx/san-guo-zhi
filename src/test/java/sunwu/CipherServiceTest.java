package sunwu;

import sunwu.service.CipherService;
import sunwu.service.CipherSummary;

public final class CipherServiceTest {
    private CipherServiceTest() {
    }

    public static void run() {
        CipherService service = new CipherService();
        CipherSummary classic = service.decryptClassic(
            "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.",
            7
        );
        TestSupport.assertEquals(
            "Advise Cao Cao to use The Chain Strategem, which is to chain his battleships with strong iron chains.",
            classic.decoded(),
            "Classic Caesar decryption should match assignment sample."
        );

        String secureEncrypted = service.encryptSecure("Attack at dawn", 3);
        String secureDecoded = service.decryptSecure(secureEncrypted);
        TestSupport.assertEquals("Attack at dawn", secureDecoded, "Secure cipher should round-trip correctly.");
    }
}
