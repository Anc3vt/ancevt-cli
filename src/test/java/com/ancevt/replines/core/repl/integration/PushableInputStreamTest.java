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

package com.ancevt.replines.core.repl.integration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PushableInputStreamTest {

    @Test
    void pushLine_shouldReturnCharactersWithNewline() throws IOException {
        PushableInputStream in = new PushableInputStream();
        in.pushLine("hello");

        List<Character> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            result.add((char) in.read());
        }

        assertEquals("hello\n", charsToString(result));
    }

    @Test
    void pushBytes_shouldReturnExactBytes() throws IOException {
        PushableInputStream in = new PushableInputStream();
        byte[] input = {65, 66, 67};
        in.pushBytes(input);

        assertEquals(65, in.read());
        assertEquals(66, in.read());
        assertEquals(67, in.read());
    }

    private String charsToString(List<Character> chars) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);
        return sb.toString();
    }
}
