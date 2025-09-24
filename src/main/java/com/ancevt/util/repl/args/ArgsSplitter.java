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
package com.ancevt.util.repl.args;

import java.util.ArrayList;
import java.util.List;

class ArgsSplitter {

    private static final String SPACE_CHARS = "\n\t\r\b ";

    static String[] split(final String source, char delimiterChar) {
        final List<String> list = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        int str = -1;
        int i = 0;
        final int len = source.length();
        while (i < len) {
            final char c = source.charAt(i++);
            if (c == '\\') {
                if (i == len) {
                    break;
                }
                final char c2 = source.charAt(i++);
                stringBuilder.append(c2);
                continue;
            }
            if (str == -1) {
                if (c == '"' || c == '\'') {
                    str = c;
                    continue;
                }

                if (delimiterChar == '\0') {
                    if (SPACE_CHARS.indexOf(c) != -1) {
                        if (stringBuilder.length() != 0) {
                            list.add(stringBuilder.toString());
                            stringBuilder.setLength(0);
                        }
                        continue;
                    }
                } else {
                    if (c == delimiterChar) {
                        if (stringBuilder.length() != 0) {
                            list.add(stringBuilder.toString());
                            stringBuilder.setLength(0);
                        }
                        continue;
                    }
                }
            } else {
                if (c == str) {
                    str = -1;
                    continue;
                }
            }
            stringBuilder.append(c);
        }
        if (stringBuilder.length() != 0) {
            list.add(stringBuilder.toString());
        }

        return list.toArray(new String[]{});
    }

    public static String[] split(String source, String delimiterChar) {
        if (delimiterChar.length() != 1) {
            throw new ArgsException("delimiter string must contain one character");
        }
        return split(source, delimiterChar.charAt(0));
    }
}
