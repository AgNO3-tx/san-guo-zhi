package sunwu;

import java.util.List;
import java.util.Objects;

/**
 * 测试断言工具，提供项目自定义测试需要的最小断言能力。
 */
public final class TestSupport {
    private TestSupport() {
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            // 抛出 AssertionError，测试运行器会统一收集并展示。
            throw new AssertionError(message);
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertListEquals(List<?> expected, List<?> actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }
}
