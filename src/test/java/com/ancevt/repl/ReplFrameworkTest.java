package com.ancevt.repl;

import com.ancevt.repl.argument.ArgumentParseException;
import com.ancevt.repl.argument.ArgumentParser;
import com.ancevt.repl.argument.ArgumentSplitHelper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ReplFrameworkTest {

    @Test
    public void testParsedArgumentsBasicParsing() {
        ArgumentParser args = ArgumentParser.parse("one two three");
        assertEquals(3, args.size());
        assertEquals("one", args.next());
        assertEquals("two", args.next());
        assertEquals("three", args.next());
    }

    @Test
    public void testParsedArgumentsQuotedParsing() {
        ArgumentParser args = ArgumentParser.parse("\"hello world\" test");
        assertEquals(2, args.size());
        assertEquals("hello world", args.next());
        assertEquals("test", args.next());
    }

    @Test
    public void testParsedArgumentsEscapeHandling() {
        ArgumentParser args = ArgumentParser.parse("line\\ break end");
        assertEquals("line break", args.next());
        assertEquals("end", args.next());
    }

    @Test
    public void testParsedArgumentsGetByKey() {
        ArgumentParser args = ArgumentParser.parse("--port 8080 --debug true");
        assertEquals(Integer.valueOf(8080), args.get(Integer.class, "--port"));
        assertEquals(Boolean.TRUE, args.get(Boolean.class, "--debug"));
    }

    @Test
    public void testParsedArgumentsIndexManagement() {
        ArgumentParser args = ArgumentParser.parse("a b c");
        args.skip(); // skip "a"
        assertEquals("b", args.next());
        args.setIndex(0);
        assertEquals("a", args.next());
    }

    @Test
    public void testCommandExecution() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        registry.register("ping", new CommandHandler() {
            public void handle(ReplRunner repl, ArgumentParser args) {
                repl.println("pong");
            }
        });

        ReplRunner repl = new ReplRunner(registry);
        ByteArrayInputStream in = new ByteArrayInputStream("ping\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        repl.start(in, out);

        String result = out.toString();
        assertTrue(result.contains("pong"));
    }

    @Test
    public void testUnknownCommandException() throws Exception {
        ReplRunner repl = new ReplRunner();
        ByteArrayInputStream in = new ByteArrayInputStream("doesnotexist\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        repl.start(in, out);

        String result = out.toString();
        assertTrue(result.contains("Unknown command"));
    }

    @Test
    public void testExitCommandStopsRepl() throws IOException {
        CommandRegistry registry = new CommandRegistry();
        registry.register("exit", new CommandHandler() {
            public void handle(ReplRunner repl, ArgumentParser args) {
                repl.stop();
            }
        });

        ReplRunner repl = new ReplRunner(registry);
        ByteArrayInputStream in = new ByteArrayInputStream("exit\nping\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        repl.start(in, out);

        String result = out.toString();
        assertFalse(result.contains("ping"));
    }

    @Test
    public void testArgumentSplitHelperWithQuotes() {
        String[] result = ArgumentSplitHelper.split("'a b' c", "\0");
        assertArrayEquals(new String[]{"a b", "c"}, result);
    }

    @Test
    public void testArgumentSplitHelperWithDelimiter() {
        String[] result = ArgumentSplitHelper.split("x,y,z", ",");
        assertArrayEquals(new String[]{"x", "y", "z"}, result);
    }

    @Test
    public void testArgumentSplitHelperInvalidDelimiter() {
        try {
            ArgumentSplitHelper.split("a|b|c", "tooLong");
            fail("Expected ArgumentParseException");
        } catch (ArgumentParseException e) {
            assertTrue(e.getMessage().contains("delimiter string must contain one character"));
        }
    }

    @Test
    public void testCommandRegistryFormattedList() {
        CommandRegistry registry = new CommandRegistry();
        registry.register("hello", "prints hello", new CommandHandler() {
            public void handle(ReplRunner repl, ArgumentParser args) {
            }
        });
        String output = registry.formattedCommandList();
        assertTrue(output.contains("hello"));
        assertTrue(output.contains("prints hello"));
    }

    @Test
    public void testCommandRegistryPrefixFilter() {
        CommandRegistry registry = new CommandRegistry();
        registry.register("start", "", new CommandHandler() {
            public void handle(ReplRunner repl, ArgumentParser args) {
            }
        });
        registry.register("stop", "", new CommandHandler() {
            public void handle(ReplRunner repl, ArgumentParser args) {
            }
        });
        String filtered = registry.formattedCommandList("sta");
        assertTrue(filtered.contains("start"));
        assertFalse(filtered.contains("stop"));
    }
}