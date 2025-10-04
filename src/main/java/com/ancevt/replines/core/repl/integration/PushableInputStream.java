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
 * <p>
 * Usage example:
 * <pre>{@code
 * PushableInputStream in = new PushableInputStream();
 * System.setIn(in);
 * in.pushLine("hello");
 * }</pre>
 * </p>
 *
 * <p><b>Thread safety:</b> the class is thread-safe — multiple threads can safely push data.</p>
 */
public class PushableInputStream extends InputStream {

    /**
     * Internal buffer queue holding input bytes.
     */
    private final BlockingQueue<Byte> queue = new LinkedBlockingQueue<>();

    /**
     * Pushes a single line of text to the stream.
     * A newline character ('\n') is automatically appended to simulate pressing "Enter".
     *
     * @param line the line of input to be added to the stream
     */
    public void pushLine(String line) {
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            queue.add(b);
        }
        queue.add((byte) '\n');
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
            return queue.take() & 0xFF;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while reading input", e);
        }
    }
}
