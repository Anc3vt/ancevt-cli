/**
 * Integration utilities for embedding REPLines in custom environments.
 * <p>
 * This package provides input and output stream wrappers that enable flexible
 * integration of the REPLines framework into GUIs, remote shells, testing frameworks,
 * or any system where standard console I/O is not sufficient.
 * </p>
 *
 * <h2>Components</h2>
 *
 * <ul>
 *   <li><b>{@link com.ancevt.replines.core.repl.io.StringFeederInputStream}</b>: A custom
 *       {@link java.io.InputStream} implementation that allows programmatic pushing of input
 *       lines into the REPL. Useful for simulating user input in testing or when embedding
 *       REPL in graphical interfaces.</li>
 *
 *   <li><b>{@link com.ancevt.replines.core.repl.io.BufferedLineOutputStream}</b>: A custom
 *       {@link java.io.OutputStream} implementation that delivers output line-by-line to a
 *       provided callback. Useful for redirecting REPL output to custom loggers, UI widgets,
 *       or remote clients.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * PushableInputStream input = new PushableInputStream();
 * LineCallbackOutputStream output = new LineCallbackOutputStream(System.out::println);
 *
 * ReplRunner repl = ReplRunner.builder()
 *     .withInputStream(input)
 *     .withOutputStream(output)
 *     .withRegistry(myRegistry)
 *     .build();
 *
 * new Thread(repl::start).start();
 *
 * input.pushLine("hello");
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * Both stream wrappers are designed to be thread-safe and can be safely accessed
 * from multiple threads simultaneously.
 *
 * <h2>Use Cases</h2>
 *
 * <ul>
 *   <li>Embedding REPL in JavaFX or Swing applications</li>
 *   <li>Creating remote-access REPL over network sockets</li>
 *   <li>Writing automated REPL tests with scripted input/output</li>
 *   <li>Logging REPL session output to files or remote monitoring systems</li>
 * </ul>
 *
 * @see com.ancevt.replines.core.repl.io.StringFeederInputStream
 * @see com.ancevt.replines.core.repl.io.BufferedLineOutputStream
 */
package com.ancevt.replines.core.repl.io;
