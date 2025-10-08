/**
 * Core utilities for parsing and binding command-line arguments.
 * <p>
 * This package provides a flexible, lightweight argument parsing engine designed for use
 * with REPLines commands. It supports both positional and named arguments, custom converters,
 * and reflection-based binding of values into fields of Java objects.
 * </p>
 *
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link com.ancevt.replines.core.argument.reflection} – tools for reflective binding of parsed arguments.</li>
 * </ul>
 *
 * <h2>Main Classes</h2>
 * <ul>
 *   <li>{@link com.ancevt.replines.core.argument.Arguments} – main parsing class for raw input strings.</li>
 *   <li>{@link com.ancevt.replines.core.argument.ArgumentParseException} – runtime exception for parse failures.</li>
 *   <li>{@link com.ancevt.replines.core.argument.ArgumentSplitHelper} – internal tokenizer used by {@code Arguments}.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Supports both "--key value" and "--key=value" syntax.</li>
 *   <li>Handles quoted strings and escaped characters.</li>
 *   <li>Provides iterator-style access and type-safe conversion methods.</li>
 *   <li>Supports primitives, enums, lists, and sets.</li>
 *   <li>Custom conversion via {@link com.ancevt.replines.core.argument.reflection.ArgumentConverter}.</li>
 * </ul>
 *
 * <h2>Integration with REPLines</h2>
 * <p>
 * Parsed arguments can be fed into the REPLines framework or automatically mapped
 * to fields in Java objects using annotations such as {@link com.ancevt.replines.core.argument.reflection.CommandArgument}
 * and {@link com.ancevt.replines.core.argument.reflection.OptionArgument}.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * class GreetCommand {
 *     @CommandArgument
 *     String name;
 *
 *     @OptionArgument(names = {"-t", "--times"})
 *     int repeat;
 * }
 *
 * Arguments args = Arguments.parse("Alice --times 3");
 * GreetCommand cmd = ArgumentBinder.convert(args, GreetCommand.class);
 * // cmd.name == "Alice"
 * // cmd.repeat == 3
 * }</pre>
 *
 * @see com.ancevt.replines.core.argument.Arguments
 * @see com.ancevt.replines.core.argument.reflection.ArgumentBinder
 * @see com.ancevt.replines.core.argument.reflection.OptionArgument
 * @see com.ancevt.replines.core.argument.reflection.CommandArgument
 */
package com.ancevt.replines.core.argument;
