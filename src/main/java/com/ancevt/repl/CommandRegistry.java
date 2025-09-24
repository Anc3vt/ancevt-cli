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
