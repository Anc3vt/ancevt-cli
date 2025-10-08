/**
 * Annotations for REPLines command discovery and execution.
 * <p>
 * This package defines the annotations used to mark classes and methods
 * for automatic discovery and registration of REPL commands within the
 * REPLines framework. It enables a declarative way to define commands,
 * reducing boilerplate and improving modularity.
 * </p>
 *
 * <h2>Primary Annotations</h2>
 *
 * <ul>
 *   <li><b>{@link com.ancevt.replines.core.repl.annotation.ReplCommand}</b>: Applied to a class to declare it
 *       as a REPL command container. The class may contain one or more methods
 *       annotated with {@code @ReplExecute} to define the actual command logic.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.annotation.ReplExecute}</b>: Applied to a method inside a
 *       {@code @ReplCommand}-annotated class. Marks this method as an executable
 *       REPL command. The method name (or a custom value) becomes the command name,
 *       and the method parameters are mapped to command-line arguments.</li>
 * </ul>
 *
 * <h2>Command Loading</h2>
 *
 * Command classes annotated with {@code @ReplCommand} can be loaded using
 * {@link com.ancevt.replines.core.repl.AnnotationCommandLoader}, which scans
 * classes and registers commands reflectively.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * @ReplCommand
 * public class MyCommands {
 *
 *     @ReplExecute
 *     public void hello() {
 *         System.out.println("Hello from annotated command!");
 *     }
 *
 *     @ReplExecute(name = "sum")
 *     public void sumCommand(int a, int b) {
 *         System.out.println("Sum: " + (a + b));
 *     }
 * }
 * }</pre>
 *
 * <h2>Parameter Mapping</h2>
 *
 * When using {@code @ReplExecute}, method parameters are automatically mapped from
 * the command-line input using REPLines' internal argument binding system. This includes
 * type conversion, default values, and validation support, especially when used in
 * conjunction with the {@code replines-core-argument} module.
 *
 * @see com.ancevt.replines.core.repl.annotation.ReplCommand
 * @see com.ancevt.replines.core.repl.annotation.ReplExecute
 * @see com.ancevt.replines.core.repl.AnnotationCommandLoader
 */
package com.ancevt.replines.core.repl.annotation;
