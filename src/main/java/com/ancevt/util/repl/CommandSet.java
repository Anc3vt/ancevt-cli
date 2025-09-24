/**
 * Copyright (C) 2022 the original author or authors.
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

import java.util.LinkedHashSet;

public class CommandSet extends LinkedHashSet<Command> {


    public Command add(String commandWord, ReplAction action) {
        Command command = new Command(commandWord, action);
        add(command);
        return command;
    }

    public Command add(String commandWord, String description, ReplAction action) {
        Command command = new Command(commandWord, description, action);
        add(command);
        return command;
    }

    public String formattedCommandList() {
        StringBuilder sb = new StringBuilder("Available commands:\n");
        for (Command command : this) {
            String word = command.getCommandWord();
            String desc = command.getDescription();
            sb.append(String.format("  %-20s %s\n", word, desc.isEmpty() ? "(no description)" : desc));
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

        for (Command command : this) {
            String word = command.getCommandWord();
            if (prefix == null || prefix.isEmpty() || word.startsWith(prefix)) {
                String desc = command.getDescription();
                sb.append(String.format("  %-20s %s\n", word, desc.isEmpty() ? "(no description)" : desc));
                anyFound = true;
            }
        }

        if (!anyFound) {
            sb.append("  (no matching commands)\n");
        }

        return sb.toString();
    }
}
