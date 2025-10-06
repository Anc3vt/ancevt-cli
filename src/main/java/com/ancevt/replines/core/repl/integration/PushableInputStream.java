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

package com.ancevt.replines.core.repl.integration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An InputStream implementation that allows manually pushing data (as lines or raw bytes)
 * to simulate user input, such as System.in in CLI or REPL environments.
 * <p>
 * Internally uses a blocking queue to hold bytes until they are read via {@link #read()}.
 * This makes it suitable for interactive applications, testing, and scenarios
 * where input must be fed programmatically in controlled fashion.
 * </p>
 *
 *
 * Usage example:
 * <pre>{@code
 * PushableInputStream in = new PushableInputStream();
 * System.setIn(in);
 * in.pushLine("hello");
 * }</pre>
 * 
 *
 * <p><b>Thread safety:</b> the class is thread-safe — multiple threads can safely push data.</p>
 */
public class PushableInputStream extends InputStream {

    private final BlockingQueue<Byte> queue = new LinkedBlockingQueue<>();
    private volatile boolean closed = false;

    /**
     * Pushes a single line of text to the stream.
     * A newline character ('\n') is automatically appended to simulate pressing "Enter".
     *
     * @param line the line of input to be added to the stream
     */
    public void pushLine(String line) {
        pushBytes((line + "\n").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Pushes an arbitrary array of bytes to the stream.
     * No newline is appended; use this for non-line-based input.
     *
     * @param bytes array of bytes to push into the input stream
     */
    public void pushBytes(byte[] bytes) {
        for (byte b : bytes) {
            queue.add(b);
        }
    }

    /**
     * Reads the next byte of data from the stream.
     * If no data is available, blocks until input becomes available.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached
     * @throws IOException if the thread is interrupted while waiting for input
     */
    @Override
    public int read() throws IOException {
        try {
            while (true) {
                if (closed && queue.isEmpty()) return -1;
                Byte b = queue.poll();
                if (b != null) return b & 0xFF;
                Thread.sleep(5);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted", e);
        }
    }

    @Override
    public void close() {
        closed = true;
    }
}
