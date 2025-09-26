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

package com.ancevt.repl.argument;

import java.util.Iterator;
import java.util.function.Consumer;

import static java.lang.String.format;

public class Arguments implements Iterable<String> {

    private final String source;
    private final String[] elements;
    private int index;
    private Throwable problem;
    private String lastContainsCheckedKey;

    public Arguments(String source) {
        this.source = source;
        elements = ArgumentSplitHelper.split(source, '\0');
    }

    public Arguments(String source, String delimiterChar) {
        this.source = source;
        elements = ArgumentSplitHelper.split(source, delimiterChar);
    }

    public Arguments(String source, char delimiterChar) {
        this.source = source;
        elements = ArgumentSplitHelper.split(source, delimiterChar);
    }

    public Arguments(String[] args) {
        this.source = collectSource(args);
        elements = args;
    }

    private String collectSource(String[] args) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String a : args) {
            a = a.replace("\"", "\\\\\"");
            stringBuilder.append('"').append(a).append('"').append(' ');
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public String[] getElements() {
        return elements;
    }

    public boolean contains(String... keys) {
        for (final String e : elements) {
            for (final String k : keys) {
                if (e.equals(k) || e.startsWith(k + "=")) {
                    lastContainsCheckedKey = k;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasNext() {
        return index < elements.length;
    }

    public void skip() {
        next();
    }

    public void skip(int count) {
        for (int i = 0; i < count; i++) next();
    }

    public String next() {
        return next(String.class);
    }

    public <T> T next(Class<T> type) {
        if (index >= elements.length) {
            throw new ArgumentParseException(format("next: Index out of bounds, index: %d, elements: %d", index, elements.length));
        }

        T result = get(type, index);
        if (result == null) {
            throw new ArgumentParseException(String.format("Args exception no such element at index %d, type: %s", index, type));
        }

        index++;
        return result;
    }

    public <T> T next(Class<T> type, T defaultValue) {
        if (index >= elements.length) {
            throw new ArgumentParseException(format("next: Index out of bounds, index: %d, elements: %d", index, elements.length));
        }

        T result = get(type, index, defaultValue);
        index++;
        return result;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index >= elements.length) {
            throw new ArgumentParseException(format("Index out of bounds, index: %d, elements: %d", index, elements.length));
        }

        this.index = index;
    }

    public void resetIndex() {
        index = 0;
    }

    public int size() {
        return elements.length;
    }

    public <T> T get(Class<T> type) {
        return get(type, lastContainsCheckedKey);
    }

    public <T> T get(Class<T> type, int index, T defaultValue) {
        if (index < 0 || index >= elements.length) return defaultValue;
        try {
            return convertToType(elements[index], type);
        } catch (Exception e) {
            problem = e;
            return defaultValue;
        }
    }

    public <T> T get(Class<T> type, int index) {
        return get(type, index, null);
    }

    public <T> T get(Class<T> type, String key, T defaultValue) {
        for (int i = 0; i < elements.length; i++) {
            final String currentArg = elements[i];

            if (currentArg.equals(key)) {
                if (i + 1 < elements.length) {
                    return convertToType(elements[i + 1], type);
                }
            }

            if (currentArg.startsWith(key + "=")) {
                return convertToType(currentArg.substring((key + "=").length()), type);
            }
        }

        return defaultValue;
    }

    public <T> T get(Class<T> type, String[] keys, T defaultValue) {
        for (final String key : keys) {
            for (int i = 0; i < elements.length; i++) {
                final String currentArg = elements[i];

                if (currentArg.equals(key)) {
                    if (i + 1 < elements.length) {
                        return convertToType(elements[i + 1], type);
                    }
                }

                if (currentArg.startsWith(key + "=")) {
                    return convertToType(currentArg.substring((key + "=").length()), type);
                }
            }
        }

        return defaultValue;
    }

    public <T> T get(Class<T> type, String key) {
        return get(type, key, null);
    }

    public <T> T get(Class<T> type, String[] keys) {
        return get(type, keys, null);
    }

    public String get(String key, String defaultValue) {
        return get(String.class, key, defaultValue);
    }

    public String get(String[] keys, String defaultValue) {
        return get(String.class, keys, defaultValue);
    }

    public String get(String key) {
        return get(String.class, key);
    }

    public String get(String[] keys) {
        return get(String.class, keys);
    }

    private <T> T convertToType(String element, Class<T> type) {
        if (type == String.class) {
            return (T) element;
        } else if (type == boolean.class || type == Boolean.class) {
            return (T) Boolean.valueOf(element.equalsIgnoreCase("true"));
        } else if (type == int.class || type == Integer.class) {
            return (T) Integer.valueOf(element);
        } else if (type == long.class || type == Long.class) {
            return (T) Long.valueOf(element);
        } else if (type == float.class || type == Float.class) {
            return (T) Float.valueOf(element);
        } else if (type == short.class || type == Short.class) {
            return (T) Short.valueOf(element);
        } else if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(element);
        } else if (type == byte.class || type == Byte.class) {
            return (T) Byte.valueOf(element);
        } else {
            throw new ArgumentParseException("Type " + type + " not supported");
        }
    }

    public String getSource() {
        return source;
    }

    public boolean isEmpty() {
        return elements == null || elements.length == 0;
    }

    public boolean hasProblem() {
        return problem != null;
    }

    public Throwable getProblem() {
        return problem;
    }

    public static Arguments parse(String source) {
        return new Arguments(source);
    }

    public static Arguments parse(String[] args) {
        return new Arguments(args);
    }

    public static Arguments parse(String source, String delimiterChar) {
        return new Arguments(source, delimiterChar);
    }

    public static Arguments parse(String source, char delimiterChar) {
        return new Arguments(source, delimiterChar);
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < elements.length;
            }

            @Override
            public String next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                return elements[i++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        for (String element : elements) {
            action.accept(element);
        }
    }
}
