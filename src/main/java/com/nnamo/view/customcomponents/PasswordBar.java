package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;


public class PasswordBar extends JPanel {
    private final JLabel label;
    private final JPasswordField passwordField;

    // CONSTRUCTOR //
    public PasswordBar() {
        super();
        label = new JLabel("Password:");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordField = new JPasswordField(20);
        passwordField.setHorizontalAlignment(JTextField.LEFT);
        passwordField.setEditable(true);
        setLayout(new GridBagLayout());
        add(label, new CustomGbc().setPosition(0, 0).setWeight(0.0, 0.0)
                .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.EAST));
        add(passwordField, new CustomGbc().setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 0, 2, 10).setAnchor(GridBagConstraints.WEST));
    }

    // GETTERS AND SETTERS //
    public String getPasswordField() {
        return new String(this.passwordField.getPassword());
    }

    public void setText(String text) {
        this.passwordField.setText(text);
    }
}
