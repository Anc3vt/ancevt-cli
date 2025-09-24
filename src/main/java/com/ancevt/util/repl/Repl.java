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

import static java.lang.String.format;

public class Repl {

    private final CommandSet commandSet;
    private boolean running;

    private InputStream inputStream;

    private OutputStream outputStream;

    public Repl(CommandSet commandSet) {
        this.commandSet = commandSet;
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


    public CommandSet getCommandSet() {
        return commandSet;
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

    public void execute(String commandLine) throws NoSuchCommandException {
        String[] tokens = commandLine.trim().split("\\s+");
        if (tokens.length == 0) return;

        String commandWord = tokens[0];

        for (Command command : getCommandSet()) {
            if (commandWord.equals(command.getCommandWord())) {
                command.execute(this, commandLine);
                return;
            }
        }

        throw new NoSuchCommandException(format("Unknown command: %s", commandWord), commandWord, commandLine, commandSet);
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
            } catch (NoSuchCommandException e) {
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
        CommandSet commandSet = new CommandSet();

        commandSet.add("test", (repl, a) -> {
            System.out.println("tested");
            String[] elements = a.getElements();
            for (int i = 0; i < elements.length; i++) {
                String element = elements[i];
                System.out.println(i + "\t" + element);
            }
        });

        commandSet.add("help", (repl, a) -> {
            System.out.println(commandSet.formattedCommandList());
        });

        commandSet.add("exit", (repl, a) -> repl.stop());

        Repl repl = new Repl(commandSet);
        repl.start(System.in, System.out);

    }
}
