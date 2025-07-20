package com.nnamo.view;

import javax.swing.*;
import java.awt.*;

public class StopPanel extends JPanel {

    JLabel labelName = new JLabel("Stop Name:");
    JTextField textName = new JTextField(20);
    JLabel labelId = new JLabel("Stop ID:");
    JTextField textID = new JTextField(20);
    JList listBus = new JList();

    // CONSTRUCTOR //
    public StopPanel() {
        super();

        this.textName.setEditable(false);
        this.textID.setEditable(false);

        setLayout(new GridLayout(3, 3));
        add(newLine(labelName, textName));
        add(newLine(labelId, textID));
        add(newLine(new JLabel("Buses at Stop:"), listBus));
        setVisible(false);
    }

    // METHODS //
    private JPanel newLine(JLabel label, Component component) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(component);
        return panel;
    }

    // GETTERS AND SETTERS //
    public JTextField getTextID() {
        return textID;
    }

    public JTextField getTextName() {
        return textName;
    }

    public JList getListBus() {
        return listBus;
    }

}
