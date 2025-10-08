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

package com.ancevt.replines.core.repl.io;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An OutputStream implementation that buffers incoming bytes until a newline character ('\n') is received.
 * When a complete line is accumulated, it invokes the provided callback with the line as a String.
 *
 * <p>Carriage return characters ('\r') are ignored. This stream is useful in REPL-like environments
 * where output is generated incrementally and needs to be processed line-by-line (e.g., shown in a UI or logged).</p>
 *
 * <p>Thread-safety: not thread-safe by design. If used across threads, wrap externally in synchronization.</p>
 *
 * <pre>{@code
 * OutputStream out = new LineCallbackOutputStream(line -> {
 *     System.out.println("Received line: " + line);
 * });
 * out.write("hello\nworld\n".getBytes());
 * }</pre>
 */
public class BufferedLineOutputStream extends OutputStream {

    private final List<Byte> byteBuffer = new ArrayList<>();

    /** Callback that receives complete lines (excluding the newline character). */
    private final Consumer<String> callback;

    /**
     * Creates a new LineCallbackOutputStream.
     *
     * @param callback a Consumer that will receive each line (without newline) when a '\n' is written
     */
    public BufferedLineOutputStream(Consumer<String> callback) {
        this.callback = callback;
    }

    /**
     * Writes a single byte to the stream. Bytes are buffered until a newline ('\n') is encountered.
     * Upon newline, the current buffer is flushed and passed to the callback.
     * Carriage return ('\r') is ignored.
     *
     * @param b the byte to write
     */
    @Override
    public void write(int b) {
        if (b == '\r') return;

        if (b == '\n') {
            flushBuffer();
        } else {
            byteBuffer.add((byte) b);
        }
    }

    /**
     * Forces flushing of the current buffer even if it hasn't ended with a newline.
     * Useful when the stream is closed or when partial lines should be delivered.
     */
    @Override
    public void flush() {
        flushBuffer();
    }

    /**
     * Internal method that delivers the current buffer to the callback if it's non-empty,
     * then clears the buffer.
     */
    private void flushBuffer() {
        if (byteBuffer.isEmpty()) return;

        byte[] bytes = new byte[byteBuffer.size()];
        for (int i = 0; i < byteBuffer.size(); i++) {
            bytes[i] = byteBuffer.get(i);
        }

        String line = new String(bytes, StandardCharsets.UTF_8);
        callback.accept(line);

        byteBuffer.clear();
    }
}
