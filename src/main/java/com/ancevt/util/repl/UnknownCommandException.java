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

public class UnknownCommandException extends Exception {

    private String commandWord;
    private String commandLine;
    private CommandRegistry registry;

    public UnknownCommandException(String message, String commandWord, String commandLine, CommandRegistry registry) {
        super(message);
        this.commandWord = commandWord;
        this.commandLine = commandLine;
        this.registry = registry;
    }

    public UnknownCommandException(String message) {
        super(message);
    }

    public UnknownCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public String getCommandWord() {
        return commandWord;
    }

    public String getCommandLine() {
        return commandLine;
    }
}
