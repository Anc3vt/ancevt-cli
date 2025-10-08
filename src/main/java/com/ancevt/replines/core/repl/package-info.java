/**
 * Core package of the REPLines framework.
 * <p>
 * This package provides the main interfaces and implementation for
 * building REPL (Read-Eval-Print Loop) systems in Java. It includes
 * the base command registration, execution, input/output processing,
 * and integration capabilities.
 * </p>
 *
 * <h2>Key Concepts</h2>
 *
 * <ul>
 *   <li><b>{@link com.ancevt.replines.core.repl.ReplRunner}</b>: The main execution loop that reads
 *       user input, parses command tokens, and executes matched commands.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.Command}</b>: Represents an individual command,
 *       including its name(s), description, and action function.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.CommandRegistry}</b>: A registry of commands.
 *       Supports manual registration and annotation-based loading.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.ReplRunnerBuilder}</b>: A fluent builder API for
 *       constructing a configured {@code ReplRunner} instance with input/output settings,
 *       filters, command sets, and more.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.ReplErrorHandler}</b>: A functional interface
 *       allowing custom exception handling logic.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.UnknownCommandException}</b>: Exception thrown
 *       when a command is not recognized by the registry.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.annotation.ReplCommand}</b> and
 *       <b>{@link com.ancevt.replines.core.repl.annotation.ReplExecute}</b>: Annotations
 *       to mark command classes and execution methods for reflection-based loading.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.io.StringFeederInputStream}</b> and
 *       <b>{@link com.ancevt.replines.core.repl.io.BufferedLineOutputStream}</b>:
 *       Utilities for stream integration, useful in embedding REPL in custom environments
 *       like GUIs or testing systems.</li>
 * </ul>
 *
 * <h2>Typical Workflow</h2>
 *
 * <pre>{@code
 * CommandRegistry registry = new CommandRegistry();
 * registry.command("hello")
 *     .description("Greets the user")
 *     .action((repl, args) -> repl.println("Hello, REPLines!"))
 *     .build();
 *
 * ReplRunner repl = ReplRunner.builder()
 *     .withRegistry(registry)
 *     .withColorizer()
 *     .withDefaultCommands()
 *     .build();
 *
 * repl.start();
 * }</pre>
 *
 * <h2>Asynchronous Execution</h2>
 *
 * Commands can be marked as asynchronous via {@link com.ancevt.replines.core.repl.Command#setAsync(boolean)}
 * or using the fluent builder API. Async commands are executed using a configurable {@link java.util.concurrent.Executor}.
 *
 * <h2>Annotation Support</h2>
 *
 * The package includes built-in support for command discovery using annotations.
 * Classes and methods can be annotated with {@link com.ancevt.replines.core.repl.annotation.ReplCommand} and
 * {@link com.ancevt.replines.core.repl.annotation.ReplExecute}, and loaded using
 * {@link com.ancevt.replines.core.repl.AnnotationCommandLoader}.
 *
 * <h2>Customization</h2>
 *
 * Developers can extend the REPL behavior by:
 * <ul>
 *   <li>Adding custom output filters via {@link com.ancevt.replines.core.repl.ReplRunner#addOutputFilter(java.util.function.Function)}</li>
 *   <li>Replacing the error handler via {@link com.ancevt.replines.core.repl.ReplRunner#setErrorHandler(ReplErrorHandler)}</li>
 *   <li>Embedding the REPL in UIs or network layers via custom I/O streams</li>
 *   <li>Extending commands using argument reflection binding (see {@code replines-core-argument} module)</li>
 * </ul>
 *
 * @see com.ancevt.replines.core.repl.ReplRunner
 * @see com.ancevt.replines.core.repl.Command
 * @see com.ancevt.replines.core.repl.CommandRegistry
 * @see com.ancevt.replines.core.repl.ReplRunnerBuilder
 * @see com.ancevt.replines.core.repl.ReplErrorHandler
 * @see com.ancevt.replines.core.repl.UnknownCommandException
 * @see com.ancevt.replines.core.repl.io.StringFeederInputStream
 * @see com.ancevt.replines.core.repl.io.BufferedLineOutputStream
 */
package com.ancevt.replines.core.repl;
