package com.nnamo.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;

import java.awt.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class StopPanel extends JPanel {
    // Stop info components
    private final JLabel labelName = new JLabel("Stop Name:");
    private final JTextField textName = new JTextField(20);
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
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 2));
        setBackground(new Color(60, 63, 65));
        // Stop info panel
        GridBagConstraints gbcStopInfo = gbcBuilder.setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5).build();
        JPanel PanelStopInfo = newStopInfoPanel();
        add(PanelStopInfo, gbcStopInfo);
        // Bus info panel
        GridBagConstraints gbcBusInfo = gbcBuilder.setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5).build();
        JPanel PanelbusInfo = newBusInfoPanel();
        add(PanelbusInfo, gbcBusInfo);
        // Route info panel
        GridBagConstraints gbcRouteInfo = gbcBuilder.setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setHeight(2).setInsets(10, 10, 5, 5).build();
        JPanel TableRouteInfo = newRouteInfoPanel();
        add(TableRouteInfo, gbcRouteInfo);
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
        // label name
        GridBagConstraints gbcLabelName = gbcBuilder.setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST).build();
        infoStop.add(labelName, gbcLabelName);
        // text name
        GridBagConstraints gbcTextName = gbcBuilder.setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST)
                .build();
        infoStop.add(textName, gbcTextName);
        // label id
        GridBagConstraints gbcLabelId = gbcBuilder.setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST).build();
        infoStop.add(labelId, gbcLabelId);
        // text id
        GridBagConstraints gbcTextID = gbcBuilder.setPosition(1, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST)
                .build();
        infoStop.add(textID, gbcTextID);
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
        // label bus
        GridBagConstraints gbcLabelBus = gbcBuilder.setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST).build();
        infoBus.add(labelBus, gbcLabelBus);
        // text bus
        GridBagConstraints gbcTextBus = gbcBuilder.setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST)
                .build();
        infoBus.add(textBus, gbcTextBus);
        // label state
        GridBagConstraints gbcLabelState = gbcBuilder.setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST).build();
        infoBus.add(labelState, gbcLabelState);
        // text state
        GridBagConstraints gbcTextState = gbcBuilder.setPosition(1, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST)
                .build();
        infoBus.add(textState, gbcTextState);
        // label posti
        GridBagConstraints gbcLabelPosti = gbcBuilder.setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST).build();
        infoBus.add(labelPosti, gbcLabelPosti);
        // text posti
        GridBagConstraints gbcTextPosti = gbcBuilder.setPosition(1, 2).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST)
                .build();
        infoBus.add(textPosti, gbcTextPosti);
        return infoBus;
    }

    private JPanel newRouteInfoPanel() {
        // Column names for the bus table
        String[] columnNames = { "Autobus", "Orario Arrivo", "Stato", "In ritardo" };
        // Model for the bus table, with non-editable cells
        busTableModel = new DefaultTableModel(columnNames, 0) {
            ;
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
            busTableModel.addRow(new Object[] { i + "", "10", "Stato" });
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

    public void updateStopTimes(List<StopTimeModel> stopTimes) {
        this.busTableModel.setRowCount(0); // Remove previous rows
        for (StopTimeModel stopTime : stopTimes) {
            LocalTime arrivalTime = LocalTime.ofInstant(stopTime.getArrivalTime().toInstant(), ZoneId.systemDefault());
            TripModel trip = stopTime.getTrip(); // Corsa

            if (trip == null) {
                continue;
            }

            RouteModel route = trip.getRoute(); // Linea
            if (route == null || route.getShortName() == null) {
                continue;
            }

            busTableModel.addRow(new Object[] {
                    route.getShortName(),
                    arrivalTime.toString(),
                    "In Orario", // DA AGGIORNARE CON DATI IN REALTIME
                    null
            });
        }
    }

    private static class Builder {
        private int gridx = 0;
        private int gridy = 0;
        private int gridwidth = 1;
        private int gridheight = 1;
        private double weightx = 0.0;
        private double weighty = 0.0;
        private int anchor = GridBagConstraints.CENTER;
        private int fill = GridBagConstraints.NONE;
        private Insets insets = new Insets(0, 0, 0, 0);
        private int ipadx = 0;
        private int ipady = 0;

        public Builder setAnchor(int anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setFill(int fill) {
            this.fill = fill;
            return this;
        }

        public Builder setHeight(int height) {
            this.gridheight = height;
            return this;
        }

        public Builder setWidth(int width) {
            this.gridwidth = width;
            return this;
        }

        public Builder setPosition(int x, int y) {
            this.gridx = x;
            this.gridy = y;
            return this;
        }

        public Builder setInsets(int top, int left, int bottom, int right) {
            this.insets = new Insets(top, left, bottom, right);
            return this;
        }

        public Builder setIpad(int ipadx, int ipady) {
            this.ipadx = ipadx;
            this.ipady = ipady;
            return this;
        }

        public Builder setWeight(double weightX, double weightY) {
            this.weightx = weightX;
            this.weighty = weightY;
            return this;
        }

        public GridBagConstraints build() {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = gridwidth;
            gbc.gridheight = gridheight;
            gbc.weightx = weightx;
            gbc.weighty = weighty;
            gbc.anchor = anchor;
            gbc.fill = fill;
            gbc.insets = insets;
            gbc.ipadx = ipadx;
            gbc.ipady = ipady;
            return gbc;
        }
    }
}
