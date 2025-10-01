/**
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
package com.ancevt.replines.core.argument.reflection;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Marks a field as an option argument.
 * <p>
 * Option arguments are specified by name, prefixed with {@code -} or {@code --},
 * and may be required or optional.
 * <p>
 * Example:
 * <pre>
 * class Example {
 *     {@literal @}OptionArgument(names = {"-p", "--port"}, required = true)
 *     int port;
 *
 *     {@literal @}OptionArgument(names = {"-v", "--verbose"})
 *     boolean verbose;
 * }
 *
 * Arguments args = Arguments.parse("--port 8080 --verbose");
 * Example ex = ArgumentBinder.convert(args, Example.class);
 * // ex.port == 8080
 * // ex.verbose == true
 * </pre>
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface OptionArgument {

    /**
     * List of option names that can be used for this argument.
     * For example: {"-p", "--port"}.
     */
    String[] names() default {};

    /**
     * Whether this option is required. If true and the option is missing,
     * {@link com.ancevt.replines.core.argument.ArgumentParseException} is thrown.
     */
    boolean required() default false;
}
