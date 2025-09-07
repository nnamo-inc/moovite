package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

/**
 * PasswordBar is a custom JPanel that contains a label and a password field.
 * It is used to input passwords in a user interface.
 *
 * @author Samuele Lombardi
 * @see JPanel
 * @see JLabel
 * @see JPasswordField
 */
public class PasswordBar extends JPanel {
    private final JLabel label;
    private final JPasswordField passwordField;

    // CONSTRUCTOR //

    /**
     * Constructs a PasswordBar with a label and a password field.
     *
     * @see JPanel
     * @see JLabel
     * @see JPasswordField
     */
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

    /**
     * Returns the label of the password field.
     *
     * @return the label of the password field
     */
    public String getPasswordField() {
        return new String(this.passwordField.getPassword());
    }

    /**
     * Sets the text of the password field.
     *
     * @param text the text to set in the password field
     */
    public void setText(String text) {
        this.passwordField.setText(text);
    }
}
