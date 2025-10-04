package com.ancevt.replines.core.repl.integration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LineCallbackOutputStreamTest {

    @Test
    void shouldCallCallbackWhenNewlineIsWritten() throws IOException {
        List<String> lines = new ArrayList<>();
        LineCallbackOutputStream out = new LineCallbackOutputStream(lines::add);

        out.write("hello\nworld\n".getBytes());

        assertEquals(2, lines.size());
        assertEquals("hello", lines.get(0));
        assertEquals("world", lines.get(1));
    }

    @Test
    void shouldIgnoreCarriageReturns() throws IOException {
        List<String> lines = new ArrayList<>();
        LineCallbackOutputStream out = new LineCallbackOutputStream(lines::add);

        out.write("a\r\nb\n".getBytes());

        assertEquals(2, lines.size());
        assertEquals("a", lines.get(0));
        assertEquals("b", lines.get(1));
    }

    @Test
    void shouldFlushPartialLineWhenFlushIsCalled() throws IOException {
        List<String> lines = new ArrayList<>();
        LineCallbackOutputStream out = new LineCallbackOutputStream(lines::add);

        out.write('p');
        out.write('i');
        out.write('n');
        out.flush();

        assertEquals(1, lines.size());
        assertEquals("pin", lines.get(0));
    }
}
