/*
 * Copyright (C) 2025 Ancevt.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
