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

package com.ancevt.cli.repl;

import com.ancevt.cli.filter.ColorizeFilter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class ReplRunnerBuilder {

    private InputStream input;
    private OutputStream output;
    private CommandRegistry registry;
    private Executor executor;
    private final List<Function<String, String>> filters = new ArrayList<>();
    private boolean useColorizer;
    private boolean addDefaultCommands;

    public ReplRunnerBuilder withInput(InputStream in) {
        this.input = in;
        return this;
    }

    public ReplRunnerBuilder withOutput(OutputStream out) {
        this.output = out;
        return this;
    }

    public ReplRunnerBuilder withRegistry(CommandRegistry registry) {
        this.registry = registry;
        return this;
    }

    public ReplRunnerBuilder withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public ReplRunnerBuilder addFilter(Function<String, String> filter) {
        filters.add(filter);
        return this;
    }

    public ReplRunnerBuilder withColorizer() {
        this.useColorizer = true;
        return this;
    }

    public ReplRunnerBuilder withDefaultCommands() {
        this.addDefaultCommands = true;
        return this;
    }

    public ReplRunner build() {
        ReplRunner repl = new ReplRunner();

        // registry
        repl.setRegistry(registry != null ? registry : new CommandRegistry());

        // streams
        if (input != null) repl.setInputStream(input);
        if (output != null) repl.setOutputStream(output);

        // executor
        if (executor != null) repl.setExecutor(executor);

        // filters
        filters.forEach(repl::addOutputFilter);
        if (useColorizer) repl.addOutputFilter(new ColorizeFilter()::colorize);

        // default commands
        if (addDefaultCommands) {
            registerDefaultCommands(repl.getRegistry());
        }

        return repl;
    }

    private static void registerDefaultCommands(CommandRegistry registry) {
        registry.command("help")
                .description("Shows help info")
                .action((r, a) -> {
                    r.println("<g>" + r.getRegistry().formattedCommandList());
                })
                .build();

        registry.command("exit", "/q")
                .description("Exit the REPL")
                .action((r, a) -> {
                    r.stop();
                })
                .build();
    }
}
