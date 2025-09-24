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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgsSplitterTest {

    @Test
    void testSplitBySpace() {
        String[] tokens = ArgsSplitter.split("one two three", '\0');
        assertArrayEquals(new String[]{"one", "two", "three"}, tokens);
    }

    @Test
    void testSplitWithQuotes() {
        String[] tokens = ArgsSplitter.split("'a b' c", '\0');
        assertArrayEquals(new String[]{"a b", "c"}, tokens);
    }

    @Test
    void testDelimiterChar() {
        String[] tokens = ArgsSplitter.split("a,b,c", ',');
        assertArrayEquals(new String[]{"a", "b", "c"}, tokens);
    }

    @Test
    void testEscapeInsideQuotes() {
        String[] tokens = ArgsSplitter.split("\"a \\\"quote\\\" inside\" b", '\0');
        assertEquals("a \"quote\" inside", tokens[0]);
        assertEquals("b", tokens[1]);
    }
}
