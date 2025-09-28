package com.ancevt.cli.repl;

import com.ancevt.cli.filter.ColorizeFilter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReplRunnerBuilder {

    private InputStream input;
    private OutputStream output;
    private CommandRegistry registry;
    private Executor executor;
    private final List<Function<String, String>> filters = new ArrayList<>();
    private final List<Consumer<CommandRegistry>> registryActions = new ArrayList<>();
    private boolean useColorizer;

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
        registryActions.add(ReplRunnerBuilder::registerDefaultCommands);
        return this;
    }

    public ReplRunnerBuilder configure(Consumer<CommandRegistry> consumer) {
        registryActions.add(consumer);
        return this;
    }

    public ReplRunner build() {
        ReplRunner repl = new ReplRunner();

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

        registry.command("exit", "/q")
                .description("Exit the REPL")
                .action((r, a) -> {
                    r.stop();
                })
                .build();
    }
}
