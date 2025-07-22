package com.nnamo.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StopPanel extends JPanel {
    // Stop info components
    private final JLabel labelName = new JLabel("Stop Name:");
    private JTextField textName = new JTextField(20);
    private final JLabel labelId = new JLabel("Stop ID:");
    private JTextField textID = new JTextField(20);
    // Bus info components
    private final JLabel labelBus = new JLabel("Bus in arrivo:");
    private JTextField textBus = new JTextField(20);
    private final JLabel labelState = new JLabel("Stato:");
    private JTextField textState = new JTextField(20);
    private final JLabel labelPosti = new JLabel("Posti disponibili:");
    private JTextField textPosti = new JTextField(20);
    // Route info components
    private JTable busTable;
    private DefaultTableModel busTableModel;

    Builder gbcBuilder = new Builder();


    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        setBackground(Color.ORANGE);
        // Stop info panel
        GridBagConstraints gbcStopInfo = gbcBuilder.setGrid(0, 0).setWeight(1.0, 0.0).build();
        JPanel StopInfoPanel = newStopInfoPanel();
        add(StopInfoPanel, gbcStopInfo);
        // Bus info panel
        GridBagConstraints gbcBusInfo = gbcBuilder.setGrid(0, 1).setWeight(1.0, 0.0).build();
        JPanel busInfoPanel = newBusInfoPanel();
        add(busInfoPanel, gbcBusInfo);
        // Route info panel
        GridBagConstraints gbcRouteInfo = gbcBuilder.setGrid(1, 0).setWeight(1.0, 0.0).setHeight(2).build();
        JPanel busInfoTable = newRouteInfoPanel();
        add(busInfoTable, gbcRouteInfo);
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

    private JPanel newRouteInfoPanel() {
        // Column names for the bus table
        String[] columnNames = {"Autobus", "Orario Arrivo", "Stato", "In ritardo"};
        // Model for the bus table, with non-editable cells
        busTableModel = new DefaultTableModel(columnNames, 0) {;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // Create the bus table with the model
        busTable = new JTable(busTableModel);
        // Create a scroll pane for the bus table
        JScrollPane scrollPane = new JScrollPane(busTable);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        // Create a panel for the bus table with a titled border
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella autobus in arrivo");
        // Add the border and the scroll pane to the panel
        panel.setBorder(border);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Just for testing
        for (int i = 0; i < 100; i++) {
            busTableModel.addRow(new Object[]{i + "", "10", "Stato"});
        }
        return panel;
    }

    // GETTERS AND SETTERS //
    public JTextField getTextID() {
        return textID;
    }

    public JTextField getTextName() {
        return textName;
    }

    private class Builder {
        private GridBagConstraints gbc;


        public Builder() {
            this.gbc = new GridBagConstraints();
            this.gbc.insets = new Insets(10, 10, 10, 10);
            this.gbc.gridx = 0;
            this.gbc.gridy = 0;
        }

        public Builder setGrid(int x, int y) {
            this.gbc.gridx = x;
            this.gbc.gridy = y;
            return this;
        }

        public Builder setIpad(int ipadx, int ipady) {
            this.gbc.ipadx = ipadx;
            this.gbc.ipady = ipady;
            return this;
        }

        public Builder setWeight(double weightX, double weightY) {
            this.gbc.weightx = weightX;
            this.gbc.weighty = weightY;
            return this;
        }

        public Builder setHeight(int height) {
            this.gbc.gridheight = height;
            return this;
        }
        
        public Builder setAnchor(int anchor) {
            this.gbc.anchor = anchor;
            return this;
        }

        public GridBagConstraints build() {
            return this.gbc;
        }
    }
}
