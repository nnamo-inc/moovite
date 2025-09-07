package com.nnamo.view.frame;

import javax.swing.*;

/**
 * A simple JFrame that displays a loading message during startup.
 *
 * @author Davide Galilei
 */
public class StartupLoadingFrame extends JFrame {
    public StartupLoadingFrame() {
        super("Processing...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null);
        JLabel processingLabel = new JLabel("Loading GTFS data, please wait...", SwingConstants.CENTER);
        add(processingLabel);
    }

    public void close() {
        dispose();
    }
}
