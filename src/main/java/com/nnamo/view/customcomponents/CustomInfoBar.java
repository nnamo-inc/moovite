package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

/**
 * Custom JPanel that create a generic information bar with a {@link JLabel} and a {@link JTextField}.
 *
 * It can be used to display information when you want a label-text relationship, such as displaying a label with a text field next to it.
 *
 * @see JPanel
 * @see JLabel
 * @see JTextField
 */
public class CustomInfoBar extends JPanel {

    // ATTRIBUTES //
    private JLabel label;
    private JTextField textField;

    // CONSTRUCTOR //

    /**
     * Creates a CustomInfoBar with a specified label name, using the.
     * @param labelName the name of the label that will be displayed in the {@link JLabel} of the {@link CustomInfoBar}.
     */
    public CustomInfoBar(String labelName) {
        super();
        setLayout(new GridBagLayout());

        // Label
        label = new JLabel(labelName);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        add(label, new CustomGbc().setPosition(0, 0).setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setWeight(0.1, 0.1));

        // Text Field
        textField = new JTextField(15);
        textField.setHorizontalAlignment(JTextField.LEFT);
        textField.setEditable(false);
        add(textField, new CustomGbc().setPosition(1, 0).setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 1.0));
    }

    // GETTERS AND SETTERS //
    /**
     * Set the text of the label.
     *
     * @param value the text to set in the label.
     */
    public void setTextField(String value) {
        this.textField.setText(value);
    }

    /**
     * Get the text from the text field.
     *
     * @return the text currently in the text field.
     */
    public String getTextField() {
        return this.textField.getText();
    }

    /**
     * Get the {@link JLabel} of the {@link CustomInfoBar}.
     *
     * @return the {@link JLabel} of the {@link CustomInfoBar}.
     */
    public JTextField getJTextField() {
        return textField;
    }
}
