package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class CustomInfoBar extends JPanel {

    private JLabel label;
    private JTextField textField;

    // CONSTRUCTOR //
    public CustomInfoBar(String labelName) {
        super();
        label = new JLabel(labelName);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        textField = new JTextField(15);
        textField.setHorizontalAlignment(JTextField.LEFT);
        textField.setEditable(false);
        setLayout(new GridBagLayout());

        add(label, new CustomGbc().setPosition(0, 0).setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setWeight(0.1, 0.1));

        add(textField, new CustomGbc().setPosition(1, 0).setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 1.0));
    }

    // GETTERS AND SETTERS //
    public JLabel getJLabel() {
        return label;
    }

    public void setTextField(String value) {
        this.textField.setText(value);
    }

    public String getTextField() {
        return this.textField.getText();
    }

    public void setJLabel(JLabel label) {
        this.label = label;
    }

    public JTextField getJTextField() {
        return textField;
    }

    public void setJTextField(JTextField text) {
        this.textField = text;
    }
}
