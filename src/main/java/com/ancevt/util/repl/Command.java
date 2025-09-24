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


import com.ancevt.util.repl.argument.ArgumentParser;

import java.util.ArrayList;
import java.util.List;

public class Command {

    private final List<String> commandWords;
    private final String description;

    private final CommandHandler action;

    public Command(List<String> commandWords, String description, CommandHandler action) {
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = description;
            this.action = action;
        }
    }

    public Command(List<String> commandWords, CommandHandler action) {
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = "";
            this.action = action;
        }
    }

    public Command(String commandWord, CommandHandler action) {
        this.commandWords = new ArrayList<>();
        this.description = "";
        this.action = action;

        commandWords.add(commandWord);
    }

    public Command(String commandWord, String description, CommandHandler action) {
        this.commandWords = new ArrayList<>();
        this.description = description;
        this.action = action;

        commandWords.add(commandWord);
    }

    public CommandHandler getAction() {
        return action;
    }

    public void execute(ReplRunner replRunner, String commandLine) {
        ArgumentParser argumentParser = ArgumentParser.parse(commandLine);
        argumentParser.skip();
        action.handle(replRunner, argumentParser);
    }

    public String getDescription() {
        return description;
    }

    public String getCommandWord() {
        return commandWords.get(0);
    }

    public List<String> getCommandWords() {
        return commandWords;
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandWords='" + commandWords + '\'' +
                ", description='" + description + '\'' +
                ", action=" + action +
                '}';
    }
}
