package com.nnamo.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class StopPanel extends JPanel {

    JLabel labelName = new JLabel("Stop Name:");
    JTextField textName = new JTextField(20);
    JLabel labelId = new JLabel("Stop ID:");
    JTextField textID = new JTextField(20);

    JLabel labelBus = new JLabel("Bus in arrivo:");
    JTextField textBus = new JTextField(20);
    JLabel labelState = new JLabel("Stato:");
    JTextField textState = new JTextField(20);
    JLabel labelPosti = new JLabel("Posti disponibili:");
    JTextField textPosti = new JTextField(20);



    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        GridBagConstraints gbcMainPanel = new GridBagConstraints();
        setBackground(Color.ORANGE);
        // Stop info panel
        gbcMainPanel.insets = new Insets(10, 10, 10, 10);
        gbcMainPanel.gridx = 0;
        gbcMainPanel.gridy = 0;
        gbcMainPanel.weightx = 1.0;
        gbcMainPanel.fill = GridBagConstraints.HORIZONTAL;
        JPanel StopInfoPanel = newStopInfoPanel();
        add(StopInfoPanel, gbcMainPanel);
        // Bus info panel
        gbcMainPanel.gridx = 0;
        gbcMainPanel.gridy = 1;
        gbcMainPanel.weightx = 1.0;
        gbcMainPanel.fill = GridBagConstraints.HORIZONTAL;
        JPanel busInfoPanel = newBusInfoPanel();
        add(busInfoPanel, gbcMainPanel);
        // set initial visibility to false
        setVisible(false);
    }

    // METHODS //
    private JPanel newStopInfoPanel() {
        // set JTextFields not editable
        textName.setEditable(false);
        textID.setEditable(false);
        // Panel info stop with border
        JPanel infoStop = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni fermata");
        infoStop.setBorder(titledBorder);
        // gbc
        GridBagConstraints gbcInfoStop = new GridBagConstraints();
        // label name
        gbcInfoStop.insets = new Insets(10, 10, 10, 10);
        gbcInfoStop.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoStop.gridx = 0;
        gbcInfoStop.gridy = 0;
        gbcInfoStop.weightx = 0.0;
        gbcInfoStop.anchor = GridBagConstraints.EAST;
        infoStop.add(labelName, gbcInfoStop);
        // text name
        gbcInfoStop.insets = new Insets(10, 10, 10, 10);
        gbcInfoStop.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoStop.gridx = 1;
        gbcInfoStop.gridy = 0;
        gbcInfoStop.weightx = 1.0;
        gbcInfoStop.anchor = GridBagConstraints.WEST;
        infoStop.add(textName, gbcInfoStop);
        // label id
        gbcInfoStop.insets = new Insets(10, 10, 10, 10);
        gbcInfoStop.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoStop.gridx = 0;
        gbcInfoStop.gridy = 1;
        gbcInfoStop.weightx = 0.0;
        gbcInfoStop.anchor = GridBagConstraints.EAST;
        infoStop.add(labelId, gbcInfoStop);
        // text id
        gbcInfoStop.insets = new Insets(10, 10, 10, 10);
        gbcInfoStop.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoStop.gridx = 1;
        gbcInfoStop.gridy = 1;
        gbcInfoStop.weightx = 1.0;
        gbcInfoStop.anchor = GridBagConstraints.WEST;
        infoStop.add(textID, gbcInfoStop);
        return infoStop;
    }

    private JPanel newBusInfoPanel() {
        // set JTextFields not editable
        textBus.setEditable(false);
        textState.setEditable(false);
        textPosti.setEditable(false);
        // Info bus panel with border
        JPanel infoBus = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni bus");
        infoBus.setBorder(titledBorder);
        // gbc
        GridBagConstraints gbcInfoBus = new GridBagConstraints();
        // label bus
        gbcInfoBus.insets = new Insets(10, 10, 10, 10);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 0;
        gbcInfoBus.gridy = 0;
        gbcInfoBus.weightx = 0.0;
        gbcInfoBus.anchor = GridBagConstraints.EAST;
        infoBus.add(labelBus, gbcInfoBus);
        // text bus
        gbcInfoBus.insets = new Insets(2, 8, 2, 8);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 1;
        gbcInfoBus.gridy = 0;
        gbcInfoBus.weightx = 1.0;
        gbcInfoBus.anchor = GridBagConstraints.WEST;
        infoBus.add(textBus, gbcInfoBus);
        // label state
        gbcInfoBus.insets = new Insets(10, 10, 10, 10);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 0;
        gbcInfoBus.gridy = 1;
        gbcInfoBus.weightx = 0.0;
        gbcInfoBus.anchor = GridBagConstraints.EAST;
        infoBus.add(labelState, gbcInfoBus);
        // text state
        gbcInfoBus.insets = new Insets(10, 10, 10, 10);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 1;
        gbcInfoBus.gridy = 1;
        gbcInfoBus.weightx = 1.0;
        gbcInfoBus.anchor = GridBagConstraints.WEST;
        infoBus.add(textState, gbcInfoBus);
        // label posti
        gbcInfoBus.insets = new Insets(10, 10, 10, 10);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 0;
        gbcInfoBus.gridy = 2;
        gbcInfoBus.weightx = 0.0;
        gbcInfoBus.anchor = GridBagConstraints.EAST;
        infoBus.add(labelPosti, gbcInfoBus);
        // text posti
        gbcInfoBus.insets = new Insets(10, 10, 10, 10);
        gbcInfoBus.fill = GridBagConstraints.HORIZONTAL;
        gbcInfoBus.gridx = 1;
        gbcInfoBus.gridy = 2;
        gbcInfoBus.weightx = 1.0;
        gbcInfoBus.anchor = GridBagConstraints.WEST;
        infoBus.add(textPosti, gbcInfoBus);
        return infoBus;
    }


    // GETTERS AND SETTERS //
    public JTextField getTextID() {
        return textID;
    }

    public JTextField getTextName() {
        return textName;
    }

}
