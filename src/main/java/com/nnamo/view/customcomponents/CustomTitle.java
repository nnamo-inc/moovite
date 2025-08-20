package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class CustomTitle extends JPanel {

    JLabel titleLabel;
    Font titleFont;

    public CustomTitle (String title) {
        titleLabel = new JLabel(title);
        titleFont = new Font("Arial", Font.BOLD, 25);
        titleLabel.setFont(titleFont);
        add(titleLabel, BorderLayout.CENTER);

    }

    public CustomTitle(String title, String fontName, int fontSize) {
        titleLabel = new JLabel(title);
        titleFont = new Font(fontName, Font.BOLD, fontSize);
    }
}
