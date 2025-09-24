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
