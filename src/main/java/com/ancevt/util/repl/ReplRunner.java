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
package com.ancevt.util.repl;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class ReplRunner {

    private CommandRegistry registry;
    private boolean running;

    private InputStream inputStream;

    private OutputStream outputStream;

    public ReplRunner() {
        this.registry = new CommandRegistry();
    }

    public ReplRunner(CommandRegistry registry) {
        this.registry = registry;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(CommandRegistry commandRegistry) {
        this.registry = commandRegistry;
    }

    public boolean isRunning() {
        return running;
    }

    public void print(Object s) {
        if (s != null) {
            try {
                outputStream.write(String.valueOf(s).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void println(Object s) {
        print(s + "\n");
    }

    public void execute(String commandLine) throws UnknownCommandException {
        String[] tokens = commandLine.trim().split("\\s+");
        if (tokens.length == 0) return;

        String commandWord = tokens[0];

        for (Command command : registry.getCommands()) {
            if (commandWord.equals(command.getCommandWord()) ||
                    command.getCommandWords().stream().anyMatch(s -> s.equals(commandWord))) {

                command.execute(this, commandLine);
                return;
            }
        }

        throw new UnknownCommandException(format("Unknown command: %s", commandWord), commandWord, commandLine, registry);
    }

    public void start(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        running = true;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while (running && (line = bufferedReader.readLine()) != null) {
            try {
                execute(line);
            } catch (UnknownCommandException e) {
                outputStream.write(e.getMessage().getBytes(StandardCharsets.UTF_8));
                outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public void start(InputStream inputStream) throws IOException {
        start(inputStream, System.out);
    }

    public void start() {
        try {
            start(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        running = false;
    }


    // Some dev sandbox
    public static void main(String[] args) throws IOException {
        ReplRunner repl = new ReplRunner();
        CommandRegistry registry = repl.getRegistry();

        registerDefaultCommands(registry, repl);
        repl.start(System.in, System.out);
    }

    private static void registerDefaultCommands(CommandRegistry registry, ReplRunner repl) {
        registry.register("test", "Prints each argument with index", (r, a) -> {
            r.println("tested");
            for (int i = 0; i < a.size(); i++) {
                r.println(i + "\t" + a.getElements()[i]);
            }
        });

        registry.register("help", "Shows help info", (r, a) -> {
            r.println(r.getRegistry().formattedCommandList());
        });

        // java 8 style of List.of instead of List.of
        registry.register(Arrays.asList("exit", "/q"), "Exit the REPL", (r, a) -> r.stop());

    }

}
