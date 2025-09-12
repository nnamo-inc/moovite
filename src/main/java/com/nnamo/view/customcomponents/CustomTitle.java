package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link JPanel} that displays a title with a specified font and size.
 *
 * @author Riccardo Finocchiaro
 * @see JPanel
 * @see JLabel
 */
public class CustomTitle extends JPanel {

    JLabel titleLabel;
    Font titleFont;

    /**
     * Creates a {@link CustomTitle} with specific title font and size for title purposes.
     *
     * @param title
     */
    public CustomTitle(String title) {
        titleLabel = new JLabel(title);
        titleFont = new Font("Arial", Font.BOLD, 25);
        titleLabel.setFont(titleFont);
        add(titleLabel, BorderLayout.CENTER);
    }
}
