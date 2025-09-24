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

class ArgsTest {

    @Test
    void testSimpleArgs() {
        Args args = Args.of("hello world");
        assertEquals(2, args.size());
        assertEquals("hello", args.next());
        assertEquals("world", args.next());
    }

    @Test
    void testQuotedArgs() {
        Args args = Args.of("\"hello world\" test");
        assertEquals(2, args.size());
        assertEquals("hello world", args.next());
        assertEquals("test", args.next());
    }

    @Test
    void testEscapeSequences() {
        Args args = Args.of("line\\ break end");
        assertEquals("line break", args.next());
        assertEquals("end", args.next());
    }

    @Test
    void testGetWithKey() {
        Args args = Args.of("--port 8080 --debug true");
        assertTrue(args.contains("--port"));
        assertEquals(8080, args.get(Integer.class, "--port"));
        assertEquals(true, args.get(Boolean.class, "--debug"));
    }

    @Test
    void testIndexHandling() {
        Args args = Args.of("one two three");
        args.skip();
        assertEquals("two", args.next());
        args.setIndex(0);
        assertEquals("one", args.next());
    }
}
