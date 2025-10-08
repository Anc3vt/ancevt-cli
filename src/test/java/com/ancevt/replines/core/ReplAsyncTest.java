/*
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

package com.ancevt.replines.core;

import com.ancevt.replines.core.repl.Command;
import com.ancevt.replines.core.repl.ReplRunner;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReplAsyncTest {
    @Test
    public void testAsyncCommandExecution() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ByteArrayOutputStream realOut = new ByteArrayOutputStream();

        OutputStream wrappedOut = new OutputStream() {
            @Override
            public void write(int b) {
                realOut.write(b);
                if (b == '\n') latch.countDown();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                realOut.write(b, off, len);
                if (new String(b, off, len).contains("done")) {
                    latch.countDown();
                }
            }

            @Override
            public String toString() {
                return realOut.toString();
            }
        };

        Command<String> command = new Command<>(
                "async",
                (repl, args) -> "done\n"
        );
        command.setAsync(true);

        ReplRunner repl = new ReplRunner();
        repl.setOutputStream(wrappedOut);

        command.executeAsync(repl, "async");

        assertTrue(latch.await(500, TimeUnit.MILLISECONDS), "Output was not written in time");

        String result = realOut.toString();
        assertTrue(result.contains("done"));
    }


    @Test
    public void testAsyncCommandWithResultAction() throws Exception {
        CompletableFuture<String> resultFuture = new CompletableFuture<>();

        Command<String> command = new Command<>(
                "async",
                (repl, args) -> "result from async",
                (repl, result) -> resultFuture.complete(result)
        );
        command.setAsync(true);

        ReplRunner repl = new ReplRunner();
        command.executeAsync(repl, "async");

        String result = resultFuture.get(500, TimeUnit.MILLISECONDS);
        assertEquals("result from async", result);
    }


    @Test
    public void testAsyncCommandHandlesException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) {
                out.write(b);
                if (b == '\n') latch.countDown();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
                if (new String(b, off, len).contains("boom")) {
                    latch.countDown();
                }
            }

            @Override
            public String toString() {
                return out.toString();
            }
        };

        Command<String> command = new Command<>(
                "failasync",
                (repl, args) -> {
                    throw new RuntimeException("boom!");
                }
        );
        command.setAsync(true);

        ReplRunner repl = new ReplRunner();
        repl.setOutputStream(outputStream);

        command.executeAsync(repl, "failasync");

        assertTrue(latch.await(500, TimeUnit.MILLISECONDS), "Exception output not captured in time");

        String result = out.toString();


        System.out.println(result);
        assertTrue(result.contains("boom"));
    }


}
