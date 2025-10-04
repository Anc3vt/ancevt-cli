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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * Core REPL (Read-Eval-Print Loop) runner.
 * Handles command execution, input/output streams, and lifecycle.
 *
 * Typical usage:
 * <pre>
 *     ReplRunner repl = ReplRunner.builder()
 *         .withDefaultCommands()
 *         .build();
 *     repl.start(System.in, System.out);
 * </pre>
 */
public class ReplRunner {

    private CommandRegistry registry;
    private boolean running;

    private InputStream inputStream;

    private OutputStream outputStream;

    private Executor executor;
    private boolean shutdownExecutorOnStop = false;

    private final List<Function<String, String>> outputFilters = new ArrayList<>();

    public ReplRunner() {
        this.registry = new CommandRegistry();
    }

    public ReplRunner(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * @return input stream used by this REPL
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Sets the input stream for reading user commands.
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * @return output stream used by this REPL
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Sets the output stream for printing results and errors.
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * @return current command registry
     */
    public CommandRegistry getRegistry() {
        return registry;
    }

    /**
     * Sets a new command registry.
     */
    public void setRegistry(CommandRegistry commandRegistry) {
        this.registry = commandRegistry;
    }

    /**
     * @return true if REPL loop is running
     */
    public boolean isRunning() {
        return running;
    }
    /**
     * Adds a filter that transforms REPL output before it is written.
     *
     * @param filter transformation function, e.g. for colorization
     */
    public void addOutputFilter(Function<String, String> filter) {
        if (filter != null) outputFilters.add(filter);
    }
    /**
     * Removes a previously added output filter.
     *
     * @return true if the filter was present and removed
     */
    public boolean removeOutputFilter(Function<String, String> filter) {
        return outputFilters.remove(filter);
    }
    /** @return an unmodifiable list of active output filters */
    public List<Function<String, String>> getOutputFilters() {
        return Collections.unmodifiableList(outputFilters);
    }
    /** Removes all output filters. */
    public void clearOutputFilters() {
        outputFilters.clear();
    }

    private String applyFilters(String text) {
        String result = text;
        for (Function<String, String> f : outputFilters) {
            result = f.apply(result);
        }
        return result;
    }
    /**
     * Prints text to the output stream without appending a newline.
     * Output filters are applied before writing.
     */
    public void print(Object s) {
        try {
            String text = String.valueOf(s);
            text = applyFilters(text);
            outputStream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Prints text to the output stream followed by a newline.
     * Output filters are applied before writing.
     */
    public void println(Object s) {
        print(s + "\n");
    }
    /**
     * Executes a single command line string.
     *
     * @param commandLine full input line
     * @throws UnknownCommandException if no matching command is found
     */
    public void execute(String commandLine) throws UnknownCommandException {
        String[] tokens = commandLine.trim().split("\\s+");
        if (tokens.length == 0) return;

        String commandWord = tokens[0];

        for (Command<?> command : registry.getCommands()) {
            if (commandWord.equals(command.getCommandWord()) ||
                    command.getCommandWords().stream().anyMatch(s -> s.equals(commandWord))) {

                if (command.isAsync()) {
                    command.executeAsync(this, commandLine);
                } else {
                    command.execute(this, commandLine);
                }
                return;
            }
        }

        throw new UnknownCommandException(format("Unknown command: %s", commandWord), commandWord, commandLine, registry);
    }
    /**
     * Starts the REPL loop using the given input and output streams.
     * Blocks until stopped or input ends.
     */
    public void start(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.running = true;

        StringBuilder sb = new StringBuilder();
        int ch;

        while (running && (ch = inputStream.read()) != -1) {
            if (ch == '\n') {
                String line = sb.toString();
                sb.setLength(0);

                try {
                    execute(line);
                } catch (UnknownCommandException e) {
                    outputStream.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                    outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                }
            } else if (ch != '\r') {
                sb.append((char) ch);
            }
        }
    }
    /**
     * Starts the REPL loop with a given input stream
     * and {@link System#out} as output.
     */
    public void start(InputStream inputStream) throws IOException {
        start(inputStream, System.out);
    }
    /** Starts the REPL loop using {@link System#in} and {@link System#out}. */
    public void start() {
        try {
            start(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /** Stops the REPL loop. */
    public void stop() {
        running = false;

        if (shutdownExecutorOnStop && executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdownNow();
        }
    }
    /** @return executor used for async command execution */
    public Executor getExecutor() {
        return executor;
    }
    /** Sets the executor for async command execution. */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    /**
     * Creates a builder for configuring and constructing {@link ReplRunner}.
     */
    public static ReplRunnerBuilder builder() {
        return new ReplRunnerBuilder();
    }

    void setShutdownExecutorOnStop(boolean value) {
        this.shutdownExecutorOnStop = value;
    }

    // Some dev sandbox
    public static void main(String[] args) throws IOException {
        ReplRunner repl = ReplRunner.builder()
                .configure(reg -> {
                    reg.command("test")
                            .description("Prints each argument with index")
                            .action((r, a) -> {
                                r.println("tested");
                                for (int i = 0; i < a.size(); i++) {
                                    r.println(i + "\t" + a.getElements()[i]);
                                }
                            })
                            .build();
                })
                .withColorizer()
                .withDefaultCommands()
                .withOutput(System.out)
                .withRegistry(new CommandRegistry())
                .build();


        repl.getRegistry().command("compute")
                .action((r, a) -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return 42;
                })
                .result((r, result) -> r.println("Answer: " + result))
                .async()
                .build();


        repl.start(System.in, System.out);

        System.out.println("END");
    }



}
