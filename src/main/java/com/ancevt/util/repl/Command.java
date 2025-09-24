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


import com.ancevt.util.repl.args.Args;

public class Command {

    private final String commandWord;
    private final String description;

    private final ReplAction action;

    public Command(String commandWord, ReplAction action) {
        this.commandWord = commandWord;
        this.description = "";
        this.action = action;
    }

    public Command(String commandWord, String description, ReplAction action) {
        this.commandWord = commandWord;
        this.description = description;
        this.action = action;
    }

    public ReplAction getAction() {
        return action;
    }

    public void execute(Repl repl, String commandLine) {
        Args args = Args.of(commandLine);
        args.skip();
        action.execute(repl, args);
    }

    public String getDescription() {
        return description;
    }

    public String getCommandWord() {
        return commandWord;
    }

    @Override
    public String toString() {
        return "Command{" +
                "commandWord='" + commandWord + '\'' +
                ", description='" + description + '\'' +
                ", action=" + action +
                '}';
    }
}
