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

import com.ancevt.replines.filter.ColorizeFilter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Builder for configuring and creating {@link ReplRunner}.
 * <p>
 * Example usage:
 * <pre>
 *     ReplRunner repl = ReplRunner.builder()
 *         .withInput(System.in)
 *         .withOutput(System.out)
 *         .withDefaultCommands()
 *         .withColorizer()
 *         .configure(reg -> {
 *             reg.command("hello")
 *                .description("Says hello")
 *                .action((r, a) -> r.println("Hello!"))
 *                .build();
 *         })
 *         .build();
 *     repl.start();
 * </pre>
 */
public class ReplRunnerBuilder {

    private InputStream input;
    private OutputStream output;
    private CommandRegistry registry;
    private Executor executor;
    private boolean autoShutdownExecutor = false;
    private boolean executorOwnedInternally = false;
    private final List<Function<String, String>> filters = new ArrayList<>();
    private final List<Consumer<CommandRegistry>> registryActions = new ArrayList<>();
    private boolean useColorizer;

    /**
     * Sets the input stream for the REPL.
     *
     * @param in input stream, e.g. {@link System#in}
     * @return this builder
     */
    public ReplRunnerBuilder withInput(InputStream in) {
        this.input = in;
        return this;
    }
    /**
     * Sets the output stream for the REPL.
     *
     * @param out output stream, e.g. {@link System#out}
     * @return this builder
     */
    public ReplRunnerBuilder withOutput(OutputStream out) {
        this.output = out;
        return this;
    }
    /**
     * Sets the command registry to use.
     * If not provided, a new empty {@link CommandRegistry} will be created.
     *
     * @param registry command registry instance
     * @return this builder
     */
    public ReplRunnerBuilder withRegistry(CommandRegistry registry) {
        this.registry = registry;
        return this;
    }
    /**
     * Sets the executor to be used for command execution.
     * <p>
     * If not set, a default cached thread pool will be used.
     * Note: If you provide your own executor, you are responsible
     * for shutting it down, unless {@link #autoShutdownExecutor(boolean)} is set to true.
     *
     * @param executor the executor to use
     * @return this builder
     */
    public ReplRunnerBuilder withExecutor(Executor executor) {
        this.executor = executor;
        this.executorOwnedInternally = false;
        return this;
    }

    /**
     * Indicates whether the executor should be automatically shut down when
     * {@link ReplRunner#stop()} is called.
     * <p>
     * This is useful when the executor is dedicated to this REPL instance.
     * Use with caution if you're sharing the executor across components.
     *
     * @param value true to auto-shutdown the executor; false otherwise
     * @return this builder
     */
    public ReplRunnerBuilder autoShutdownExecutor(boolean value) {
        this.autoShutdownExecutor = value;
        return this;
    }
    /**
     * Adds an output filter that will be applied to all printed text.
     *
     * @param filter transformation function (e.g. colorizer)
     * @return this builder
     */
    public ReplRunnerBuilder addFilter(Function<String, String> filter) {
        filters.add(filter);
        return this;
    }
    /**
     * Enables the built-in colorizer filter.
     * This replaces tags like {@code <red>} with ANSI escape codes.
     *
     * @return this builder
     */
    public ReplRunnerBuilder withColorizer() {
        this.useColorizer = true;
        return this;
    }
    /**
     * Registers default commands such as {@code help} and {@code exit}.
     *
     * @return this builder
     */
    public ReplRunnerBuilder withDefaultCommands() {
        registryActions.add(ReplRunnerBuilder::registerDefaultCommands);
        return this;
    }
    /**
     * Adds a configuration action that receives the {@link CommandRegistry}.
     * Useful for registering custom commands.
     *
     * @param consumer registry configuration callback
     * @return this builder
     */
    public ReplRunnerBuilder configure(Consumer<CommandRegistry> consumer) {
        registryActions.add(consumer);
        return this;
    }

    /**
     * Builds and returns a new {@link ReplRunner} with the configured options.
     *
     * @return configured ReplRunner
     */
    public ReplRunner build() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });
            executorOwnedInternally = true;
        }

        ReplRunner repl = new ReplRunner();
        repl.setShutdownExecutorOnStop(autoShutdownExecutor || executorOwnedInternally);
        repl.setExecutor(executor);

        // registry
        CommandRegistry finalRegistry = (registry != null ? registry : new CommandRegistry());
        registryActions.forEach(action -> action.accept(finalRegistry));
        repl.setRegistry(finalRegistry);

        // streams
        if (input != null) repl.setInputStream(input);
        if (output != null) repl.setOutputStream(output);

        // executor
        if (executor != null) repl.setExecutor(executor);

        // filters
        filters.forEach(repl::addOutputFilter);
        if (useColorizer) repl.addOutputFilter(new ColorizeFilter()::colorize);

        return repl;
    }

    private static void registerDefaultCommands(CommandRegistry registry) {
        registry.command("help")
                .description("Shows help info")
                .action((r, a) -> {
                    r.println("<g>" + r.getRegistry().formattedCommandList());
                })
                .build();

        registry.command("exit")
                .description("Exit the REPL")
                .action((r, a) -> {
                    r.stop();
                })
                .build();
    }
}
