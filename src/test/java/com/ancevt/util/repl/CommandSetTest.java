package com.ancevt.util.repl;

import com.ancevt.util.repl.Command;
import com.ancevt.util.repl.CommandSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandSetTest {

    @Test
    void testAddCommand() {
        CommandSet set = new CommandSet();
        Command c = set.add("ping", "pings the server", (repl, args) -> {});
        assertTrue(set.contains(c));
        assertEquals("ping", c.getCommandWord());
    }

    @Test
    void testFormattedOutput() {
        CommandSet set = new CommandSet();
        set.add("cmd", "description", (repl, args) -> {});
        String out = set.formattedCommandList();
        assertTrue(out.contains("cmd"));
        assertTrue(out.contains("description"));
    }

    @Test
    void testFilterWithPrefix() {
        CommandSet set = new CommandSet();
        set.add("start", "", (repl, args) -> {});
        set.add("stop", "", (repl, args) -> {});
        set.add("status", "", (repl, args) -> {});
        String filtered = set.formattedCommandList("sta");
        assertTrue(filtered.contains("start"));
        assertTrue(filtered.contains("status"));
        assertFalse(filtered.contains("stop"));
    }
}
