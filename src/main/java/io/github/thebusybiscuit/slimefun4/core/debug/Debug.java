package io.github.thebusybiscuit.slimefun4.core.debug;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * This class is responsible for debug logging.
 * Server owners can enable testing specific cases and have debug logs for those cases.
 * <p>
 * <b>Note:</b> We don't have validates in here because we want it to be quick and it's mainly for us internal devs.
 *
 * @author WalshyDev
 */
/*
 * 为了防止有附属使用该类，故不删除
 */
public final class Debug {
    @Getter
    private static final List<String> testCase = new ArrayList<>();

    private Debug() {}

    /**
     * Log a message if the {@link TestCase} is currently enabled.
     *
     * @param testCase
     *            The {@link TestCase} to use
     * @param msg
     *            The message to log
     */
    public static void log(TestCase testCase, String msg) {}

    /**
     * Log a variable message if the {@link TestCase} is currently enabled.
     *
     * @param testCase
     *            The {@link TestCase} to use
     * @param msg
     *            The message to log
     * @param vars
     *            The variables to replace, use "{}" in the message and have it replaced with a specified thing
     */
    public static void log(TestCase testCase, String msg, Object... vars) {}

    /**
     * Log a message if the test case is currently enabled.
     *
     * @param test
     *            The test case to use
     * @param msg
     *            The message to log
     */
    public static void log(String test, String msg) {}

    /**
     * Log a message if the test case is currently enabled.
     *
     * @param test
     *            The test case to use
     * @param msg
     *            The message to log
     * @param vars
     *            The variables to replace, use "{}" in the message and have it replaced with a specified thing
     */
    public static void log(String test, String msg, Object... vars) {}

    /**
     * Format the message. Replace "{}" with the supplied variable. This is quick and works great.
     * <p>
     * <code>
     * Benchmark                    Mode  Cnt        Score       Error  Units
     * MyBenchmark.loopAllChars    thrpt    5  2336518.563 ± 24129.488  ops/s
     * MyBenchmark.whileFindChars  thrpt    5  3319022.018 ± 45663.898  ops/s
     * </code>
     *
     * @param msg
     *            The message to send. For variables, you can pass "{}"
     * @param vars
     *            A varargs of the variables you wish to use
     *
     * @return The resulting String
     */
    private static String formatMessage(String msg, Object... vars) {
        int i = 0;
        int idx = 0;

        // Find an opening curly brace `{` and validate the next char is a closing one `}`
        while ((i = msg.indexOf('{', i)) != -1 && msg.charAt(i + 1) == '}') {
            // Substring up to the opening brace `{`, add the variable for this and add the rest of the
            // message
            msg = msg.substring(0, i) + vars[idx] + msg.substring(i + 2);
            i += String.valueOf(vars[idx++]).length();
        }

        return msg;
    }

    /**
     * Set the current test case for this server.
     * This will enable debug logging for this specific case which can be helpful by Slimefun or addon developers.
     *
     * @param test The test case to enable or null to disable it
     */
    public static void addTestCase(@Nullable String test) {
        testCase.add(test);
    }

    public static boolean hasTestCase(TestCase tc) {
        return testCase.contains(tc.toString());
    }

    public static void disableTestCase() {
        testCase.clear();
    }
}
