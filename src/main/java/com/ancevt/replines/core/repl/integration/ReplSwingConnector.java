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

import com.ancevt.replines.core.repl.ReplRunner;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Connects a {@link ReplRunner} to a Swing-based UI using a {@link JTextField} for input
 * and a {@link JTextArea} for output, enabling interactive command-line REPL experiences.
 * <p>
 * User input is collected from the input field and injected into a simulated input stream,
 * while REPL output is captured and displayed line-by-line in the output area.
 * The REPL runs asynchronously in a background thread.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ReplSwingConnector.launch(inputField, outputArea, replRunner, true);
 * }</pre>
 */
public class ReplSwingConnector {

    private final JTextField inputField;
    private final JTextArea outputArea;
    private final PushableInputStream inputStream = new PushableInputStream();
    private final OutputStream outputStream;
    private final ReplRunner replRunner;
    private final boolean clearOnEnter;

    /**
     * Constructs a new connector between a REPL and Swing input/output components.
     *
     * @param inputField   JTextField used for user input (e.g. command line)
     * @param outputArea   JTextArea used to display REPL output (e.g. results, logs)
     * @param replRunner   an instance of {@link ReplRunner} to be executed
     * @param clearOnEnter if true, clears the input field after Enter is pressed
     */
    public ReplSwingConnector(JTextField inputField,
                              JTextArea outputArea,
                              ReplRunner replRunner,
                              boolean clearOnEnter) {
        this.inputField = inputField;
        this.outputArea = outputArea;
        this.replRunner = replRunner;
        this.clearOnEnter = clearOnEnter;

        this.outputStream = new LineCallbackOutputStream(line -> {
            if (line != null && !line.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    outputArea.append(line + "\n");
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                });
            }
        });

        initListeners();
    }

    /**
     * Initializes the input field to push input lines into the REPL input stream.
     * Any previous ActionListeners are removed and replaced.
     */
    private void initListeners() {
        ActionListener action = e -> {
            String command = inputField.getText();
            inputStream.pushLine(command);
            if (clearOnEnter) inputField.setText("");
        };

        inputField.addActionListener(action);
    }

    /**
     * Starts the REPL runner in a background thread using the configured input and output streams.
     * Errors during startup are displayed in the output area.
     */
    public void startRepl() {
        CompletableFuture.runAsync(() -> {
            try {
                replRunner.start(inputStream, outputStream);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    outputArea.append("REPL error: " + e.getMessage() + "\n");
                });
            }
        });
    }

    /**
     * Utility method that creates a new {@link ReplSwingConnector}, attaches it to the UI,
     * and immediately starts the REPL.
     *
     * @param input       the input field for user commands
     * @param output      the output area for displaying results
     * @param replRunner  the REPL runner to use
     * @param clearInput  whether to clear the input field after each command
     * @return the created and running {@code ReplSwingConnector} instance
     */
    public static ReplSwingConnector launch(JTextField input,
                                            JTextArea output,
                                            ReplRunner replRunner,
                                            boolean clearInput) {
        ReplSwingConnector connector = new ReplSwingConnector(input, output, replRunner, clearInput);
        connector.startRepl();
        return connector;
    }
}
