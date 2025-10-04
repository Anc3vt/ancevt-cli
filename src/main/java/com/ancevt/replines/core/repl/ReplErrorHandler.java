package com.ancevt.replines.core.repl;

/**
 * Functional interface for handling REPL-level errors.
 * <p>
 * A {@link ReplErrorHandler} allows customizing how exceptions are reported,
 * logged, or transformed. It is invoked whenever an unhandled error occurs
 * during command execution or parsing.
 *
 * <p>Example:</p>
 * <pre>{@code
 * repl.setErrorHandler((runner, error) -> {
 *     runner.println("<red>Error:</red> " + error.getMessage());
 *     error.printStackTrace();
 * });
 * }</pre>
 */
@FunctionalInterface
public interface ReplErrorHandler {

    /**
     * Invoked when an exception occurs during REPL operation.
     *
     * @param repl  current REPL runner
     * @param throwable the thrown exception or error
     */
    void handle(ReplRunner repl, Throwable throwable);
}
