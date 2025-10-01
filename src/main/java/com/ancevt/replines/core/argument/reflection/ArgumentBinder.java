/*
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
package com.ancevt.replines.core.argument.reflection;

import com.ancevt.replines.core.argument.ArgumentParseException;
import com.ancevt.replines.core.argument.Arguments;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Utility class that binds parsed {@link Arguments} to fields of a target object
 * using reflection and annotations {@link CommandArgument} and {@link OptionArgument}.
 * <p>
 * Example:
 * <pre>
 * class MyCommand {
 *     {@literal @}CommandArgument
 *     String name;
 *
 *     {@literal @}OptionArgument(names = {"-c", "--count"}, required = true)
 *     int count;
 * }
 *
 * Arguments args = Arguments.parse("hello -c 5");
 * MyCommand cmd = ArgumentBinder.convert(args, MyCommand.class);
 * // cmd.name == "hello"
 * // cmd.count == 5
 * </pre>
 */
public class ArgumentBinder {

    /**
     * Fills fields of an existing object with values from {@link Arguments}.
     *
     * @param args         parsed arguments
     * @param objectToFill existing instance to bind values into
     * @param <T>          type of target object
     * @return same instance with bound values
     * @throws IllegalAccessException      if a field is not accessible
     * @throws ArgumentParseException      if a required option argument is missing
     */
    public static <T> T convert(Arguments args, T objectToFill) throws IllegalAccessException {
        Class<T> type = (Class<T>) objectToFill.getClass();

        for (Field field : type.getDeclaredFields()) {

            CommandArgument commandArgumentAnnotation = field.getDeclaredAnnotation(CommandArgument.class);
            if (commandArgumentAnnotation != null) {
                Class<?> t = field.getType();
                field.setAccessible(true);
                field.set(objectToFill, args.get(t, 0));
                continue;
            }

            OptionArgument optionArgumentAnnotation = field.getDeclaredAnnotation(OptionArgument.class);
            if (optionArgumentAnnotation != null) {

                boolean found = false;

                String[] names = optionArgumentAnnotation.names();
                if (names != null) {
                    for (String name : names) {
                        if (args.contains(name)) {
                            field.setAccessible(true);

                            Class<?> fieldType = field.getType();

                            if (fieldType == boolean.class) {
                                field.set(objectToFill, args.contains(name));
                            } else {
                                field.set(objectToFill, args.get(field.getType(), name));
                            }
                            found = true;
                        }
                    }
                }

                if (optionArgumentAnnotation.required() && !found)
                    throw new ArgumentParseException("required parameter " + Arrays.toString(names) + " not found");

                continue;
            }
        }

        return objectToFill;
    }

    /**
     * Creates a new instance of the given class (using no-arg constructor),
     * and fills its fields with values from {@link Arguments}.
     *
     * @param args parsed arguments
     * @param type class to instantiate and bind
     * @param <T>  type of target object
     * @return a new instance with bound values
     * @throws NoSuchMethodException       if no default constructor is found
     * @throws InvocationTargetException   if constructor throws exception
     * @throws InstantiationException      if instance cannot be created
     * @throws IllegalAccessException      if field or constructor access fails
     * @throws ArgumentParseException      if a required option argument is missing
     */
    public static <T> T convert(Arguments args, Class<T> type)
        throws NoSuchMethodException,
        InvocationTargetException,
        InstantiationException,
        IllegalAccessException {

        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return convert(args, constructor.newInstance());
    }

}
