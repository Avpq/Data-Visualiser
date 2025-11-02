package com.avez.visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUIWrapper extends JFrame {
    private JTextArea outputArea;
    private JTextField inputField;
    private PipedOutputStream pipedOut;
    private PipedInputStream pipedIn;
    private volatile boolean cliRunning = true;

    public GUIWrapper() {
        setTitle("CSV Visualizer");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create output area (displays CLI output)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(new Color(0, 255, 0));
        outputArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create input field (user types here)
        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setBackground(new Color(40, 40, 40));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(" > "), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.setBackground(new Color(40, 40, 40));

        // Handle user input
        inputField.addActionListener(e -> {
            String input = inputField.getText();
            if (!input.isEmpty()) {
                try {
                    // Only write if CLI is still running
                    if (cliRunning && pipedOut != null) {
                        // Send input to CLI
                        pipedOut.write((input + System.lineSeparator()).getBytes());
                        pipedOut.flush();

                        // Clear input field
                        inputField.setText("");
                    }
                } catch (IOException ex) {
                    // Pipe is closed, CLI has finished
                    cliRunning = false;
                    inputField.setEnabled(false);
                }
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Setup piped streams for input
        try {
            pipedIn = new PipedInputStream();
            pipedOut = new PipedOutputStream(pipedIn);

            // Redirect System.in to read from our pipe
            System.setIn(pipedIn);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error setting up I/O streams: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Redirect System.out to text area with UTF-8 encoding
        try {
            PrintStream printStream = new PrintStream(new OutputStream() {
                private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                @Override
                public void write(int b) throws IOException {
                    if (b == '\n' || b == '\r') {
                        flush();
                        if (b == '\n') {
                            SwingUtilities.invokeLater(() -> outputArea.append("\n"));
                        }
                    } else {
                        buffer.write(b);
                    }
                }

                @Override
                public void flush() throws IOException {
                    if (buffer.size() > 0) {
                        String text = buffer.toString("UTF-8");
                        buffer.reset();
                        SwingUtilities.invokeLater(() -> {
                            outputArea.append(text);
                            outputArea.setCaretPosition(outputArea.getDocument().getLength());
                        });
                    }
                }
            }, true, "UTF-8");

            System.setOut(printStream);
            System.setErr(printStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setVisible(true);

        // Focus on input field
        inputField.requestFocusInWindow();
    }

    // Method to clean up when CLI finishes
    public void onCLIFinished() {
        cliRunning = false;
        SwingUtilities.invokeLater(() -> {
            inputField.setEnabled(false);
            inputField.setText("CLI has finished - you may close this window");
        });

        // Close the piped streams
        try {
            if (pipedOut != null) {
                pipedOut.close();
            }
            if (pipedIn != null) {
                pipedIn.close();
            }
        } catch (IOException e) {
            // Ignore - streams already closed
        }
    }

    public static void main(String[] args) {
        // Must run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GUIWrapper gui = new GUIWrapper();

            // Start CLI in background thread AFTER GUI is visible
            new Thread(() -> {
                try {
                    // Give GUI time to initialize
                    Thread.sleep(500);

                    // Initialize UTF-8 encoding for Unicode support
                    UIRenderer.initializeEncoding();

                    // Start the CLI
                    CLIInterface cli = new CLIInterface();
                    cli.start();

                    // CLI has finished - clean up
                    gui.onCLIFinished();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error starting CLI: " + e.getMessage());
                    gui.onCLIFinished();
                }
            }).start();
        });
    }
}
