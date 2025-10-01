/*
 * Copyright (C) 2025 Ancevt.
 */
package com.ancevt.replines.core.repl;

import com.ancevt.replines.core.argument.Arguments;
import com.ancevt.replines.core.argument.reflection.CommandArgument;
import com.ancevt.replines.core.argument.reflection.OptionArgument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandBuilderBindActionTest {

    static class EchoArgs {
        @CommandArgument
        String text;

        @OptionArgument(names = {"-n", "--num"})
        int number;
    }

    @Test
    void testActionWithFunctionReturnsValue() {
        Command<String> cmd = Command.<String>builder("echo")
                .action(EchoArgs.class, (repl, parsed) -> {
                    // проверим, что биндинг сработал
                    assertEquals("hello", parsed.text);
                    assertEquals(42, parsed.number);
                    return "OK-" + parsed.text;
                })
                .build();

        // подменим ReplRunner-заглушку
        ReplRunner dummy = new ReplRunner() {
            public void println(String s) { /* no-op */ }
        };

        Arguments args = Arguments.parse("hello -n 42");
        args.skip(); // пропустить ключевое слово
        String result = cmd.getAction().apply(dummy, args);

        assertEquals("OK-hello", result);
    }

    @Test
    void testActionWithConsumerExecutesSideEffect() {
        StringBuilder output = new StringBuilder();

        Command<Void> cmd = Command.<Void>builder("echo")
                .action(EchoArgs.class, (repl, parsed) -> {
                    output.append(parsed.text).append(":").append(parsed.number);
                })
                .build();

        ReplRunner dummy = new ReplRunner() {
            public void println(String s) { /* no-op */ }
        };

        Arguments args = Arguments.parse("world --num 7");
        args.skip();
        cmd.getAction().apply(dummy, args); // выполнится consumer

        assertEquals("world:7", output.toString());
    }



}
