/**
 * Provides reflection-based argument binding utilities for the Replines command framework.
 * <p>
 * This package contains:
 * <ul>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.ArgumentBinder} —
 *       utility to bind parsed {@link com.ancevt.replines.core.argument.Arguments}
 *       into annotated fields of a Java object using reflection.</li>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.CommandArgument} —
 *       annotation for marking positional command arguments.</li>
 *   <li>{@link com.ancevt.replines.core.argument.reflection.OptionArgument} —
 *       annotation for marking named option arguments (e.g., {@code -p}, {@code --port}).</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * class MyCommand {
 *     @CommandArgument
 *     private String name;
 *
 *     @OptionArgument(names = {"-c", "--count"}, required = true)
 *     private int count;
 * }
 *
 * Arguments args = Arguments.parse("hello --count 5");
 * MyCommand cmd = ArgumentBinder.convert(args, MyCommand.class);
 * // cmd.name == "hello"
 * // cmd.count == 5
 * }</pre>
 *
 * <p>Typical usage:
 * <ol>
 *   <li>Define a class with fields annotated with {@code @CommandArgument} and/or {@code @OptionArgument}.</li>
 *   <li>Parse arguments into an {@link com.ancevt.replines.core.argument.Arguments} instance.</li>
 *   <li>Use {@link com.ancevt.replines.core.argument.reflection.ArgumentBinder#convert} to create and fill the object.</li>
 * </ol>
 */
package com.ancevt.replines.core.argument.reflection;
