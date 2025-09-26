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
        registry.register("ping", (repl, args) -> repl.println("pong"));

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
        registry.register("exit", (repl, args) -> repl.stop());

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
        registry.register("hello", "prints hello", (repl, args) -> {
        });
        String output = registry.formattedCommandList();
        assertTrue(output.contains("hello"));
        assertTrue(output.contains("prints hello"));
    }

    @Test
    public void testCommandRegistryPrefixFilter() {
        CommandRegistry registry = new CommandRegistry();
        registry.register("start", "", (repl, args) -> {
        });
        registry.register("stop", "", (repl, args) -> {
        });
        String filtered = registry.formattedCommandList("sta");
        assertTrue(filtered.contains("start"));
        assertFalse(filtered.contains("stop"));
    }

    @Test
    public void testKeyEqualsValueSyntax() {
        ArgumentParser args = ArgumentParser.parse("--port=1234 --debug=true");
        assertEquals(1234, (int) args.get(Integer.class, "--port"));
        assertEquals(true, args.get(Boolean.class, "--debug"));
    }

    @Test
    public void testKeyEqualsValueMixedSyntax() {
        ArgumentParser args = ArgumentParser.parse("--host localhost --port=8080");
        assertEquals("localhost", args.get(String.class, "--host"));
        assertEquals(8080, (int) args.get(Integer.class, "--port"));
    }

    @Test
    public void testKeyEqualsValueWithArrayAccess() {
        ArgumentParser args = ArgumentParser.parse("--mode=fast --retries 5");
        assertEquals("fast", args.get(new String[]{"--mode", "--m"}));
        assertEquals(5, (int) args.get(Integer.class, new String[]{"--retries"}));
    }

    @Test
    public void testContainsMethodWithEqualsSyntax() {
        ArgumentParser args = ArgumentParser.parse("--config=config.json --force");
        assertTrue(args.contains("--config"));
        assertTrue(args.contains("--force"));
        assertFalse(args.contains("--notfound"));
    }

    @Test
    public void testGetUnknownKeyReturnsDefaultValue() {
        ArgumentParser args = ArgumentParser.parse("--port=9000");
        assertEquals("localhost", args.get(String.class, "--host", "localhost"));
        assertEquals(9000, (int) args.get(Integer.class, "--port", 1234));
        assertEquals(1234, (int) args.get(Integer.class, "--missing", 1234));
    }

}