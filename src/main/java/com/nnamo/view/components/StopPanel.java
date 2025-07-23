package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class StopPanel extends JPanel {
    // Stop info components
    private final JLabel labelName = new JLabel("Stop Name:");
    private final JTextField textName = new JTextField(20);
    private final JLabel labelId = new JLabel("Stop ID:");
    private final JTextField textID = new JTextField(20);
    // Bus info components
    private final JLabel labelBus = new JLabel("Bus in arrivo:");
    private final JTextField textBus = new JTextField(20);
    private final JLabel labelState = new JLabel("Stato:");
    private final JTextField textState = new JTextField(20);
    private final JLabel labelPosti = new JLabel("Posti disponibili:");
    private final JTextField textPosti = new JTextField(20);
    // Route info components
    private JTable tableBus;
    private DefaultTableModel tableModelBus;
    private final JLabel labelSearchBus = new JLabel("Search Bus:");
    private JTextField textSearchBus = new JTextField(20);
    // Prefer components
    private JButton buttonPreferStop = new JButton("Stop");
    private JButton buttonPreferRoute = new JButton("Route");

    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 2));
        setBackground(new Color(60, 63, 65));
        // Stop info panel
        JPanel PanelStopInfo = newStopInfoPanel();
        add(PanelStopInfo, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5));
        // Bus info panel
        JPanel PanelbusInfo = newBusInfoPanel();
        add(PanelbusInfo, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 5));
        // Route info panel
        JPanel TableRouteInfo = newRouteInfoPanel();
        add(TableRouteInfo, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));
        // Buttons prefer
        JPanel PanelPrefer = newPanelPrefer();
        add(PanelPrefer, new GbcCustom().setPosition(2, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));
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
        infoStop.add(labelName, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.EAST)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        // text name
        infoStop.add(textName, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        // label id
        infoStop.add(labelId, new GbcCustom().setPosition(0, 1).setAnchor(GridBagConstraints.EAST)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        // text id
        infoStop.add(textID, new GbcCustom().setPosition(1, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));
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
        infoBus.add(labelBus, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST));
        // text bus
        infoBus.add(textBus, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));
        // label state
        infoBus.add(labelState, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST));
        // text state
        infoBus.add(textState, new GbcCustom().setPosition(1, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));
        // label posti
        infoBus.add(labelPosti, new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.EAST));
        // text posti
        infoBus.add(textPosti, new GbcCustom().setPosition(1, 2).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));
        return infoBus;
    }

    private JPanel newRouteInfoPanel() {
        String[] columnNames = { "Autobus", "Orario Arrivo", "Stato", "In ritardo" };

        tableModelBus = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBus = new JTable(tableModelBus);

        JScrollPane scrollPane = new JScrollPane(tableBus);
        scrollPane.setPreferredSize(new Dimension(400, 100));

        JPanel mainPanel = new JPanel(new BorderLayout());
        TitledBorder border = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella autobus in arrivo");
        mainPanel.setBorder(border);
        JPanel northPanel = new JPanel(new GridBagLayout());

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        // label search bus
        northPanel.add(labelSearchBus, new GbcCustom().setPosition(0, 0)
                .setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));
        // text search bus
        northPanel.add(textSearchBus, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.WEST));

        TableRowSorter sorter = new TableRowSorter(tableModelBus);
        sorter.setSortable(1, false);
        tableBus.setRowSorter(sorter);

        textSearchBus.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = textSearchBus.getText().trim();
                if (searchText.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("^" + searchText, 0));
                }
            }
        });

        return mainPanel;
    }

    private JPanel newPanelPrefer() {
        JPanel panelPrefer = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Preferiti");
        panelPrefer.setBorder(titledBorder);
        // button prefer
        panelPrefer.add(buttonPreferStop, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));
        panelPrefer.add(buttonPreferRoute, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));
        return panelPrefer;
    }

    // GETTERS AND SETTERS //
    public JTextField getTextID() {
        return textID;
    }

    public JTextField getTextName() {
        return textName;
    }

    public void updateStopTimes(List<StopTimeModel> stopTimes) {
        this.tableModelBus.setRowCount(0); // Remove previous rows
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

            tableModelBus.addRow(new Object[] {
                    route.getShortName(),
                    arrivalTime.toString(),
                    "In Orario", // DA AGGIORNARE CON DATI IN REALTIME
                    null
            });
        }
    }

/*    private static class Builder {
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
    }*/
}
