package com.ancevt.cli.repl;

import com.ancevt.cli.filter.ColorizeFilter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Function;

public class ReplRunnerBuilder {

    private final ReplRunner replRunner;

    public ReplRunnerBuilder() {
        this.replRunner = new ReplRunner();
    }

    public ReplRunnerBuilder withInput(InputStream in) {
        replRunner.setInputStream(in);
        return this;
    }

    public ReplRunnerBuilder withOutput(OutputStream out) {
        replRunner.setOutputStream(out);
        return this;
    }

    public ReplRunnerBuilder withRegistry(CommandRegistry registry) {
        replRunner.setRegistry(registry);
        return this;
    }

    public ReplRunnerBuilder withExecutor(java.util.concurrent.Executor executor) {
        replRunner.setExecutor(executor);
        return this;
    }

    public ReplRunnerBuilder addFilter(Function<String, String> filter) {
        replRunner.addOutputFilter(filter);
        return this;
    }

    public ReplRunnerBuilder withColorizer() {
        replRunner.addOutputFilter(new ColorizeFilter()::colorize);
        return this;
    }

    public ReplRunner build() {
        return replRunner;
    }

    public ReplRunnerBuilder addCommand(Command<?> command) {
        replRunner.getRegistry().register(command);
        return this;
    }

    public ReplRunnerBuilder addCommands(Command<?>... commands) {
        Arrays.stream(commands).forEach(replRunner.getRegistry()::register);
        return this;
    }

    public ReplRunnerBuilder withDefaultCommands() {
        CommandRegistry registry = replRunner.getRegistry();

        registry.command("test")
                .description("Prints each argument with index")
                .action((r, a) -> {
                    r.println("tested");
                    for (int i = 0; i < a.size(); i++) {
                        r.println(i + "\t" + a.getElements()[i]);
                    }
                })
                .build();

        registry.command("help")
                .description("Shows help info")
                .action((r, a) -> {
                    r.println("<g>" + r.getRegistry().formattedCommandList());
                }).build();

        registry.command("exit", "/q")
                .description("Exit the REPL")
                .action((r, a) -> {
                    r.stop();
                })
                .build();

        return this;
    }

}

