package com.nnamo.view;

import javax.swing.*;

public class StopPanel extends JPanel {

    JLabel labelName = new JLabel("Stop Name:");
    JTextField textName = new JTextField(20);
    JLabel labelId = new JLabel("Stop ID:");
    JTextField textID = new JTextField(20);
    JList listBus = new JList();

    // CONSTRUCTOR //
    public StopPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(labelName);
        add(textName);
        add(labelId);
        add(textID);
        add(listBus);
        setVisible(false);
    }

    // METHODS //


    // GETTERS AND SETTERS //
    public JTextField getTextID() {
        return textID;
    }

    public JTextField getTextName() {
        return textName;
    }

}
