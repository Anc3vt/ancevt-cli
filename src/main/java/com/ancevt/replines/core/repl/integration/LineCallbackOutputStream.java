package com.ancevt.replines.core.repl.integration;

import java.io.OutputStream;
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
public class LineCallbackOutputStream extends OutputStream {

    /** Internal buffer to accumulate characters until newline. */
    private final StringBuilder buffer = new StringBuilder();

    /** Callback that receives complete lines (excluding the newline character). */
    private final Consumer<String> callback;

    /**
     * Creates a new LineCallbackOutputStream.
     *
     * @param callback a Consumer that will receive each line (without newline) when a '\n' is written
     */
    public LineCallbackOutputStream(Consumer<String> callback) {
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
            buffer.append((char) b);
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
        if (buffer.length() == 0) return;
        callback.accept(buffer.toString());
        buffer.setLength(0);
    }
}
