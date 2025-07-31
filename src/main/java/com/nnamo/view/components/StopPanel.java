package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.services.RealtimeStopUpdate;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.InfoBar;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.custompreferbutton.CustomPreferButton;

import java.awt.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StopPanel extends JPanel {
    // Stop info components
    private final InfoBar nomeFermata = new InfoBar("Nome fermata: ");
    private final InfoBar idFermata = new InfoBar("ID fermata: ");
    // Bus info components
    private final InfoBar busInArrivo = new InfoBar("Autobus in arrivo: ");
    private final InfoBar statoBusInArrivo = new InfoBar("Stato autobus in arrivo: ");
    private final InfoBar numeroPosti = new InfoBar("Numero posti: ");
    // Route info components
    private CustomTable table;
    // Prefer components
    private CustomPreferButton favoriteStopButton = new CustomPreferButton("Fermata");
    private CustomPreferButton favoriteRouteButton = new CustomPreferButton("Linea");

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
        JPanel tableRouteInfo = newRouteInfoPanel();
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella corse");
        tableRouteInfo.setBorder(titledBorder);
        add(tableRouteInfo, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));

        // Buttons prefer
        JPanel PanelPrefer = newPanelPrefer();
        add(PanelPrefer, new GbcCustom().setPosition(2, 0).setWeight(1.0, 1.0).setHeight(2)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 5, 5));

        // inizialize the listeners
        setVisible(false);
    }

    // METHODS //
    private JPanel newStopInfoPanel() {
        // Panel info stop with border
        JPanel infoStop = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni fermata");
        infoStop.setBorder(titledBorder);

        // label name
        infoStop.add(nomeFermata,
                new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // text name
        infoStop.add(idFermata, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        return infoStop;
    }

    private JPanel newBusInfoPanel() {
        // Info bus panel with border
        JPanel infoBus = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni bus");
        infoBus.setBorder(titledBorder);

        // label bus
        infoBus.add(busInArrivo,
                new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // text bus
        infoBus.add(statoBusInArrivo,
                new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        // label state
        infoBus.add(numeroPosti,
                new GbcCustom().setPosition(0, 2).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));

        return infoBus;
    }

    private JPanel newRouteInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(100, 400));
        this.table = new CustomTable(new String[] { "Autobus", "Orario Arrivo", "Stato", "In ritardo" }, true);

        // Table
        mainPanel.add(table, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10));

        table.getRowSorter().setSortable(1, false);

        return mainPanel;
    }

    private JPanel newPanelPrefer() {
        JPanel panelPrefer = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Preferiti");
        panelPrefer.setBorder(titledBorder);
        // button prefer
        panelPrefer.add(favoriteStopButton, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));

        favoriteRouteButton.setEnabled(false);
        panelPrefer.add(favoriteRouteButton, new GbcCustom().setPosition(0, 1).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH).setInsets(10, 10, 10, 10).setAnchor(GridBagConstraints.CENTER));
        return panelPrefer;
    }

    public void updateStopTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        table.clear();

        HashMap<String, RealtimeStopUpdate> realtimeTrips = new HashMap<>();
        for (RealtimeStopUpdate update : realtimeUpdates) {
            System.out.println("Adding realtime update for trip ID: " + update.getTripId());
            realtimeTrips.put(update.getTripId(), update);
        }

        for (StopTimeModel stopTime : stopTimes) {
            LocalTime arrivalTime = LocalTime.ofInstant(stopTime.getArrivalTime().toInstant(), ZoneId.systemDefault());
            TripModel trip = stopTime.getTrip(); // Corsa
            if (trip == null)
                continue;
            System.out.println("Processing stop time of trip... " + trip.getId());

            RouteModel route = trip.getRoute(); // Linea
            if (route == null || route.getShortName() == null) {
                continue;
            }

            RealtimeStopUpdate timeUpdate = realtimeTrips.get(trip.getId());
            if (timeUpdate != null) {
                System.out.println(
                        "Realtime update for trip ID: " + trip.getId() + " - Arrival Time: "
                                + new Date(timeUpdate.getUpdateTime()).toString());
            }
            table.addRow(new Object[] {
                    route.getShortName(),
                    arrivalTime.toString(),
                    "In Orario", // TODO: DA AGGIORNARE CON DATI IN REALTIME
                    null
            });
        }
    }

    public void updateStopPanelInfo(String id, String nome) {
        this.nomeFermata.setTextField(nome);
        this.idFermata.setTextField(id);
    }

    public void updateFavoriteRouteMessage(String string) {
        favoriteRouteButton.setText(string);
    }
    // GETTERS AND SETTERS //

    public JTextField getTextID() {
        return idFermata.getJTextField();
    }

    public JTextField getTextName() {
        return nomeFermata.getJTextField();
    }

    public void updatePreferButtons(boolean favorite) {
        favoriteStopButton.setFavorite(favorite);
        favoriteStopButton.setItemId(idFermata.getTextField());
        updateFavoriteRouteMessage("Clicca una linea sulla tabella degli orari");
    }

    public void updatePreferRouteButton(boolean isFavorite, String routeId) {
        favoriteRouteButton.setFavorite(isFavorite);
        favoriteRouteButton.setItemId(routeId);
        favoriteRouteButton.setEnabled(true);
    }

    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteStopButton.setFavBehaviour(behaviour);
        }
    }

    public void setFavRouteBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteRouteButton.setFavBehaviour(behaviour);
        }
    }

    public void setTableClickListener(TableRowClickListener tableRowClickListener) {
        if (tableRowClickListener != null) {
            this.table.setTableRowClickListener(tableRowClickListener);
        }
    }

    public boolean isRouteButtonEnabled() {
        return favoriteRouteButton.isEnabled();
    }
}
