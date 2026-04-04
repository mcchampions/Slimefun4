package io.github.thebusybiscuit.slimefun4.core.debug;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class Debug {

    /**
     * -- GETTER --
     *  Get the current test case for this server or null if disabled
     *
     */
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

    public static void addTestCase(@Nullable String test) {}

    public static boolean hasTestCase(TestCase tc) {
        return false;
    }

    public static void disableTestCase(){}
}