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
import com.ancevt.cli.repl.annotation.ReplCommand;
import com.ancevt.cli.repl.annotation.ReplExecute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;

class AnnotationCommandLoader {

    public static void load(CommandRegistry registry, Object... instances) {
        for (Object instance : instances) {
            load(registry, instance);
        }
    }

    public static void load(CommandRegistry registry, List<Object> instances) {
        for (Object instance : instances) {
            load(registry, instance);
        }
    }

    public static void load(CommandRegistry registry, Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            load(registry, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate class: " + clazz.getName(), e);
        }
    }

    public static void loadClass(CommandRegistry registry, List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            load(registry, clazz);
        }
    }

    public static void loadClass(CommandRegistry registry, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            load(registry, clazz);
        }
    }


    public static void load(CommandRegistry registry, Object instance) {
        if (instance == null) return;
        Class<?> clazz = instance.getClass();

        // Кейс 1: класс — это команда (execute method внутри)
        if (clazz.isAnnotationPresent(ReplCommand.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ReplExecute.class)) {
                    registerCommand(clazz.getAnnotation(ReplCommand.class), method, instance, registry);
                }
            }
        }

        // Кейс 2: методы-команды
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ReplCommand.class)) {
                registerCommand(method.getAnnotation(ReplCommand.class), method, instance, registry);
            }
        }
    }

    private static void registerCommand(ReplCommand cmdAnn, Method method, Object instance, CommandRegistry registry) {
        method.setAccessible(true);

        BiFunction<ReplRunner, Arguments, Object> action = (repl, args) -> {
            try {
                return method.invoke(instance, repl, args);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute command: " + cmdAnn.name(), e);
            }
        };

        Command<Object> command = new Command<>(
                cmdAnn.name(),
                cmdAnn.description(),
                action
        );

        command.setAsync(cmdAnn.async());
        registry.register(command);
    }
}
