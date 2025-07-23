package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class InfoBar extends JPanel {

    private JLabel label;
    private JTextField text;

    // CONSTRUCTOR //
    public InfoBar(String labelName) {
        super();
        label = new JLabel(labelName);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        text = new JTextField(20);
        text.setHorizontalAlignment(JTextField.LEFT);
        text.setEditable(false);
        setLayout(new GridBagLayout());
        add(label, new GbcCustom().setPosition(0, 0).setWeight(0.0, 0.0)
                .setInsets(2, 10, 2, 0).setAnchor(GridBagConstraints.EAST));
        add(text, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 0, 2, 10).setAnchor(GridBagConstraints.WEST));
    }

    public InfoBar(String labelText, String preview) {
        super();
        label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        text = new JTextField(preview, 20);
        text.setCaretColor(Color.GRAY);
        text.setHorizontalAlignment(JTextField.LEFT);
        text.setEditable(false);
        setLayout(new GridBagLayout());
        add(label, new GbcCustom().setPosition(0, 0).setWeight(0.0, 0.0)
                .setInsets(2, 10, 2, 0).setAnchor(GridBagConstraints.EAST));
        add(text, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 0, 2, 10).setAnchor(GridBagConstraints.WEST));
    }

    // GETTERS AND SETTERS //
    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public JTextField getText() {
        return text;
    }

    public void setText(JTextField text) {
        this.text = text;
    }
}
