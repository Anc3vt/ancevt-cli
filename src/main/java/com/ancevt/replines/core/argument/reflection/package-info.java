/**
 * Reflection-based argument binding utilities for REPLines commands.
 * <p>
 * This subpackage enables automatic population of Java objects from parsed arguments
 * using annotations. It eliminates the need for manual argument extraction and conversion,
 * supporting both positional and named parameters.
 * </p>
 *
 * <h2>Annotations</h2>
 * <ul>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.CommandArgument} – Marks a field as a positional argument.</li>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.OptionArgument} – Marks a field as a named (flag or option) argument.</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.ArgumentBinder} – Binds parsed {@link com.ancevt.replines.core.argument.Arguments}
 *       to fields annotated with {@code @CommandArgument} and {@code @OptionArgument}.</li>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.ArgumentConverter} – Functional interface for custom type conversion.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Supports primitive types, enums, lists, sets, and custom types.</li>
 *   <li>Supports required/optional flags with validation.</li>
 *   <li>Custom converters for complex domain-specific data.</li>
 *   <li>Can be used with both mutable and immutable POJOs (with default constructors).</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * class Config {
 *     @CommandArgument
 *     String filename;
 *
 *     @OptionArgument(names = {"-m", "--mode"}, required = true)
 *     Mode mode;
 * }
 *
 * enum Mode { DEV, PROD }
 *
 * Arguments args = Arguments.parse("config.yaml --mode dev");
 * Config config = ArgumentBinder.convert(args, Config.class);
 * }</pre>
 *
 * @see com.ancevt.replines.core.argument.reflection.ArgumentBinder
 * @see com.ancevt.replines.core.argument.reflection.CommandArgument
 * @see com.ancevt.replines.core.argument.reflection.OptionArgument
 * @see com.ancevt.replines.core.argument.reflection.ArgumentConverter
 */
package com.ancevt.replines.core.argument.reflection;
