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
