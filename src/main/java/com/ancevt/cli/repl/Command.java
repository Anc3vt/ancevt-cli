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
package com.ancevt.cli.repl;


import com.ancevt.cli.argument.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Command<T> {

    private final List<String> commandWords;
    private final String description;

    private final BiFunction<ReplRunner, Arguments, T> action;

    private BiConsumer<ReplRunner, T> resultAction;

    private boolean isAsync;

    public Command(List<String> commandWords, String description, BiFunction<ReplRunner, Arguments, T> action) {
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = description;
            this.action = action;
        }
    }

    public Command(List<String> commandWords, BiFunction<ReplRunner, Arguments, T> action) {
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = "";
            this.action = action;
        }
    }

    public Command(String commandWord, BiFunction<ReplRunner, Arguments, T> action) {
        this.commandWords = new ArrayList<>();
        this.description = "";
        this.action = action;

        commandWords.add(commandWord);
    }

    public Command(String commandWord, String description, BiFunction<ReplRunner, Arguments, T> action) {
        this.commandWords = new ArrayList<>();
        this.description = description;
        this.action = action;

        commandWords.add(commandWord);
    }

    public Command(List<String> commandWords, String description, BiFunction<ReplRunner, Arguments, T> action, BiConsumer<ReplRunner, T> resultAction) {
        this.resultAction = resultAction;
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = description;
            this.action = action;
        }
    }

    public Command(List<String> commandWords, BiFunction<ReplRunner, Arguments, T> action, BiConsumer<ReplRunner, T> resultAction) {
        this.resultAction = resultAction;
        if (commandWords.isEmpty()) {
            throw new IllegalArgumentException("commandWords must not be empty");
        } else {
            this.commandWords = commandWords;
            this.description = "";
            this.action = action;
        }
    }

    public Command(String commandWord, BiFunction<ReplRunner, Arguments, T> action, BiConsumer<ReplRunner, T> resultAction) {
        this.resultAction = resultAction;
        this.commandWords = new ArrayList<>();
        this.description = "";
        this.action = action;

        commandWords.add(commandWord);
    }

    public Command(String commandWord, String description, BiFunction<ReplRunner, Arguments, T> action, BiConsumer<ReplRunner, T> resultAction) {
        this.resultAction = resultAction;
        this.commandWords = new ArrayList<>();
        this.description = description;
        this.action = action;

        commandWords.add(commandWord);
    }

    public BiFunction<ReplRunner, Arguments, T> getAction() {
        return action;
    }

    public void setResultAction(BiConsumer<ReplRunner, T> resultAction) {
        this.resultAction = resultAction;
    }

    public BiConsumer<ReplRunner, T> getResultAction() {
        return resultAction;
    }

    public void execute(ReplRunner replRunner, String commandLine) {
        Arguments arguments = Arguments.parse(commandLine);
        arguments.skip();
        T result = action.apply(replRunner, arguments);
        if (resultAction != null) resultAction.accept(replRunner, result);
    }

    public void executeAsync(ReplRunner replRunner, String commandLine) {
        Runnable task = () -> {
            try {
                Arguments arguments = Arguments.parse(commandLine);
                arguments.skip();
                T result = action.apply(replRunner, arguments);

                if (result != null) {
                    if (resultAction != null) {
                        resultAction.accept(replRunner, result);
                    } else {
                        replRunner.println(String.valueOf(result));
                    }
                }

            } catch (Exception e) {
                replRunner.println("Async exception: " + e.getMessage());
            }
        };

        Executor executor = replRunner.getExecutor();

        if (executor != null) {
            CompletableFuture.runAsync(task, executor);
        } else {
            CompletableFuture.runAsync(task); // fallback: commonPool
        }
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

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public static <T> Builder<T> builder(String commandWord) {
        return new Builder<>(commandWord);
    }

    public static <T> Builder<T> builder(List<String> commandWords) {
        return new Builder<>(commandWords);
    }

    public static <T> Builder<T> builder(String... commandWords) {
        if (commandWords.length == 1) {
            return new Builder<>(commandWords[0]);
        } else {
            return new Builder<>(Arrays.asList(commandWords));
        }
    }

    public static class Builder<T> {
        private final List<String> commandWords = new ArrayList<>();
        private String description = "";
        private BiFunction<ReplRunner, Arguments, T> action;
        private BiConsumer<ReplRunner, T> resultAction;
        private boolean async = false;

        public Builder(String... words) {
            this.commandWords.addAll(Arrays.asList(words));
        }

        public Builder(List<String> words) {
            this.commandWords.addAll(words);
        }

        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<T> action(BiFunction<ReplRunner, Arguments, T> action) {
            this.action = action;
            return this;
        }

        public Builder<T> action(BiConsumer<ReplRunner, Arguments> consumer) {
            this.action = (r, a) -> {
                consumer.accept(r, a);
                return null;
            };
            return this;
        }

        public Builder<T> result(BiConsumer<ReplRunner, T> resultAction) {
            this.resultAction = resultAction;
            return this;
        }

        public Builder<T> async() {
            this.async = true;
            return this;
        }

        public Command<T> build() {
            Command<T> command = new Command<>(commandWords, description, action, resultAction);
            command.setAsync(async);
            return command;
        }

        public void register(CommandRegistry registry) {
            registry.register(build());
        }
    }


}
