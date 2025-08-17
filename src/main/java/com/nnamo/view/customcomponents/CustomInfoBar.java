package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class CustomInfoBar extends JPanel {

    // ATTRIBUTES //
    private JLabel label;
    private JTextField textField;

    // CONSTRUCTOR //
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
    public void setTextField(String value) {
        this.textField.setText(value);
    }

    public String getTextField() {
        return this.textField.getText();
    }

    public JTextField getJTextField() {
        return textField;
    }
}
