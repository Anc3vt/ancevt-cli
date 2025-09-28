package com.ancevt.cli;

import com.ancevt.cli.argument.ArgumentParseException;
import com.ancevt.cli.argument.Arguments;
import com.ancevt.cli.argument.ArgumentSplitHelper;
import com.ancevt.cli.repl.CommandRegistry;
import com.ancevt.cli.repl.ReplRunner;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ReplFrameworkTest {

    @Test
    public void testParsedArgumentsBasicParsing() {
        Arguments args = Arguments.parse("one two three");
        assertEquals(3, args.size());
        assertEquals("one", args.next());
        assertEquals("two", args.next());
        assertEquals("three", args.next());
    }

    @Test
    public void testParsedArgumentsQuotedParsing() {
        Arguments args = Arguments.parse("\"hello world\" test");
        assertEquals(2, args.size());
        assertEquals("hello world", args.next());
        assertEquals("test", args.next());
    }

    @Test
    public void testParsedArgumentsEscapeHandling() {
        Arguments args = Arguments.parse("line\\ break end");
        assertEquals("line break", args.next());
        assertEquals("end", args.next());
    }

    @Test
    public void testParsedArgumentsGetByKey() {
        Arguments args = Arguments.parse("--port 8080 --debug true");
        assertEquals(Integer.valueOf(8080), args.get(Integer.class, "--port"));
        assertEquals(Boolean.TRUE, args.get(Boolean.class, "--debug"));
    }

    @Test
    public void testParsedArgumentsIndexManagement() {
        Arguments args = Arguments.parse("a b c");
        args.skip(); // skip "a"
        assertEquals("b", args.next());
        args.setIndex(0);
        assertEquals("a", args.next());
    }

    @Test
    public void testCommandExecution() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        registry.command("ping")
                .description("simple ping command")
                .action((repl, args) -> {
                    repl.println("pong");
                    return 0;
                })
                .build();

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
        registry.command("exit")
                .description("stop repl")
                .action((repl, args) -> {
                    repl.stop();
                })
                .build();

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
        registry.command("hello")
                .description("prints hello")
                .action((repl, args) -> {
                    repl.println("hello");
                })
                .build();

        String output = registry.formattedCommandList();
        assertTrue(output.contains("hello"));
        assertTrue(output.contains("prints hello"));
    }

    @Test
    public void testCommandRegistryPrefixFilter() {
        CommandRegistry registry = new CommandRegistry();
        registry.command("start").action((r, a) -> {}).build();
        registry.command("stop").action((r, a) -> {}).build();

        String filtered = registry.formattedCommandList("sta");
        assertTrue(filtered.contains("start"));
        assertFalse(filtered.contains("stop"));
    }

    @Test
    public void testKeyEqualsValueSyntax() {
        Arguments args = Arguments.parse("--port=1234 --debug=true");
        assertEquals(1234, (int) args.get(Integer.class, "--port"));
        assertEquals(true, args.get(Boolean.class, "--debug"));
    }

    @Test
    public void testKeyEqualsValueMixedSyntax() {
        Arguments args = Arguments.parse("--host localhost --port=8080");
        assertEquals("localhost", args.get(String.class, "--host"));
        assertEquals(8080, (int) args.get(Integer.class, "--port"));
    }

    @Test
    public void testKeyEqualsValueWithArrayAccess() {
        Arguments args = Arguments.parse("--mode=fast --retries 5");
        assertEquals("fast", args.get(new String[]{"--mode", "--m"}));
        assertEquals(5, (int) args.get(Integer.class, new String[]{"--retries"}));
    }

    @Test
    public void testContainsMethodWithEqualsSyntax() {
        Arguments args = Arguments.parse("--config=config.json --force");
        assertTrue(args.contains("--config"));
        assertTrue(args.contains("--force"));
        assertFalse(args.contains("--notfound"));
    }

    @Test
    public void testGetUnknownKeyReturnsDefaultValue() {
        Arguments args = Arguments.parse("--port=9000");
        assertEquals("localhost", args.get(String.class, "--host", "localhost"));
        assertEquals(9000, (int) args.get(Integer.class, "--port", 1234));
        assertEquals(1234, (int) args.get(Integer.class, "--missing", 1234));
    }
}
