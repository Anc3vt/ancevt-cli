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

package com.ancevt.repl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CommandRegistry {

    private final Set<Command> commands = new LinkedHashSet<>();

    public CommandRegistry register(List<String> commandWords, CommandHandler action) {
        Command command = new Command(commandWords, action);
        commands.add(command);
        return this;
    }

    public CommandRegistry register(List<String> commandWords, String description, CommandHandler action) {
        Command command = new Command(commandWords, description, action);
        commands.add(command);
        return this;
    }

    public CommandRegistry register(String commandWord, CommandHandler action) {
        Command command = new Command(commandWord, action);
        commands.add(command);
        return this;
    }

    public CommandRegistry register(String commandWord, String description, CommandHandler action) {
        Command command = new Command(commandWord, description, action);
        commands.add(command);
        return this;
    }

    public String formattedCommandList() {
        StringBuilder sb = new StringBuilder("Available commands:\n");
        for (Command command : commands) {
            String word = command.getCommandWord();
            String desc = command.getDescription();
            sb.append(String.format("  %-20s %s\n", word, desc.isEmpty() ? "" : desc));
        }
        return sb.toString();
    }

    public String formattedCommandList(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands");
        if (prefix != null && !prefix.isEmpty()) {
            sb.append(" starting with '").append(prefix).append("'");
        }
        sb.append(":\n");

        boolean anyFound = false;

        for (Command command : commands) {
            String word = command.getCommandWord();
            if (prefix == null || prefix.isEmpty() || word.startsWith(prefix)) {
                String desc = command.getDescription();
                sb.append(String.format("  %-20s %s\n", word, desc.isEmpty() ? "" : desc));
                anyFound = true;
            }
        }

        if (!anyFound) {
            sb.append("  (no matching commands)\n");
        }

        return sb.toString();
    }

    public Set<Command> getCommands() {
        return commands;
    }
}
