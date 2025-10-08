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

package com.ancevt.replines.core.repl.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedLineOutputStreamTest {

    @Test
    void shouldCallCallbackWhenNewlineIsWritten() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedLineOutputStream out = new BufferedLineOutputStream(lines::add);

        out.write("hello\nworld\n".getBytes());

        assertEquals(2, lines.size());
        assertEquals("hello", lines.get(0));
        assertEquals("world", lines.get(1));
    }

    @Test
    void shouldIgnoreCarriageReturns() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedLineOutputStream out = new BufferedLineOutputStream(lines::add);

        out.write("a\r\nb\n".getBytes());

        assertEquals(2, lines.size());
        assertEquals("a", lines.get(0));
        assertEquals("b", lines.get(1));
    }

    @Test
    void shouldFlushPartialLineWhenFlushIsCalled() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedLineOutputStream out = new BufferedLineOutputStream(lines::add);

        out.write('p');
        out.write('i');
        out.write('n');
        out.flush();

        assertEquals(1, lines.size());
        assertEquals("pin", lines.get(0));
    }
}
