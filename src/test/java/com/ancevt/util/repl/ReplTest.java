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

import com.ancevt.util.repl.*;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ReplTest {

    @Test
    void testExecuteKnownCommand() throws Exception {
        CommandSet set = new CommandSet();
        set.add("ping", (repl, args) -> repl.println("pong"));

        Repl repl = new Repl(set);

        ByteArrayInputStream in = new ByteArrayInputStream("ping\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        repl.start(in, out);

        String result = out.toString();
        assertTrue(result.contains("pong"));
    }

    @Test
    void testUnknownCommand() throws Exception {
        CommandSet set = new CommandSet(); // empty
        Repl repl = new Repl(set);

        ByteArrayInputStream in = new ByteArrayInputStream("doesnotexist\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        repl.start(in, out);

        String result = out.toString();
        assertTrue(result.contains("Unknown command"));
    }

    @Test
    void testExitCommand() throws Exception {
        CommandSet set = new CommandSet();
        set.add("exit", (repl, args) -> repl.stop());

        Repl repl = new Repl(set);

        ByteArrayInputStream in = new ByteArrayInputStream("exit\nping\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        repl.start(in, out);

        // "ping" не выполнится, потому что REPL остановится после "exit"
        String result = out.toString();
        assertFalse(result.contains("ping"));
    }
}
