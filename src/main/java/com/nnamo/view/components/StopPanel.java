package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nnamo.enums.ButtonMode;
import com.nnamo.enums.ColumnName;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomInfoBar;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomPreferButton;

import java.awt.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;

public class StopPanel extends JPanel {
    // Stop info components
    private final CustomInfoBar nomeFermata = new CustomInfoBar("Nome fermata: ");
    private final CustomInfoBar idFermata = new CustomInfoBar("ID fermata: ");
    // Bus info components
    private final CustomInfoBar busInArrivo = new CustomInfoBar("In arrivo: ");
    private final CustomInfoBar statoBusInArrivo = new CustomInfoBar("Stato: ");
    private final CustomInfoBar numeroPosti = new CustomInfoBar("Numero posti: ");
    // Route info components
    private CustomTable table;
    // Prefer components
    private final CustomPreferButton favoriteStopButton = new CustomPreferButton("Fermata", ButtonMode.BOTH);
    private final CustomPreferButton favoriteRouteButton = new CustomPreferButton("Linea", ButtonMode.BOTH);

    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 2));
        setBackground(new Color(60, 63, 65));

        // Stop info panel
        JPanel PanelStopInfo = newStopInfoPanel();
        add(PanelStopInfo, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.1).setWidth(2)
                .setInsets(2, 5, 2, 5));

//        // Bus info panel
//        JPanel PanelbusInfo = newBusInfoPanel();
//        add(PanelbusInfo, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.WEST)
//                .setFill(GridBagConstraints.BOTH).setWeight(0.9, 0.9)
//                .setInsets(2, 5, 2, 5));

        // Route info panel
        JPanel tableRouteInfo = newRouteInfoPanel();
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella corse");
        tableRouteInfo.setBorder(titledBorder);
        add(tableRouteInfo,
                new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.WEST)
                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
                        .setInsets(2, 5, 2, 5));

        // Buttons prefer
        JPanel PanelPrefer = newPanelPrefer();
        add(PanelPrefer,
                new CustomGbc().setPosition(1, 1).setAnchor(GridBagConstraints.EAST)
                        .setFill(GridBagConstraints.BOTH).setWeight(0.1, 0.1)
                        .setInsets(2, 5, 2, 5));

        // inizialize the listeners
        setVisible(false);
    }

    // COMPONENT METHODS //
    private JPanel newStopInfoPanel() {
        // Panel info stop with border
        JPanel infoStop = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni fermata");
        infoStop.setBorder(titledBorder);

        // label name
        infoStop.add(nomeFermata,
                new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.WEST)
                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0));

        // text name
        infoStop.add(idFermata, new CustomGbc().setPosition(1, 0).setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.BOTH).setWeight(0.3, 1.0));

        return infoStop;
    }

//    private JPanel newBusInfoPanel() {
//        // Info bus panel with border
//        JPanel infoBus = new JPanel(new GridBagLayout());
//        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Informazioni bus");
//        infoBus.setBorder(titledBorder);
//
//        // label bus
//        infoBus.add(busInArrivo,
//                new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.WEST)
//                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
//                        .setInsets(2, 5, 2, 5));
//        // text bus
//        infoBus.add(statoBusInArrivo,
//                new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.WEST)
//                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
//                        .setInsets(2, 5, 2, 5));
//        // label state
//        infoBus.add(numeroPosti,
//                new CustomGbc().setPosition(0, 2).setAnchor(GridBagConstraints.WEST)
//                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
//                        .setInsets(2, 5, 2, 5));
//
//        return infoBus;
//    }

    private JPanel newRouteInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        this.table = new CustomTable( new ColumnName[]{LINEA, DIREZIONE, ORARIO, STATO, MINUTIRIMAMENTI, POSTIDISPONIBILI, TIPO}, LINEA);
        table.setSearchColumns(LINEA, DIREZIONE, ORARIO);
        // Table
        mainPanel.add(table, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        return mainPanel;
    }

    private JPanel newPanelPrefer() {

        JPanel panelPrefer = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Preferiti");
        panelPrefer.setBorder(titledBorder);

        Dimension minButtonSize = new Dimension(100, Integer.MAX_VALUE);
        favoriteStopButton.setMinimumSize(minButtonSize);
        favoriteRouteButton.setMinimumSize(minButtonSize);
        // button prefer
        panelPrefer.add(favoriteStopButton, new CustomGbc().setPosition(0, 0)
                .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0));

        favoriteRouteButton.setEnabled(false);
        panelPrefer.add(favoriteRouteButton, new CustomGbc().setPosition(0, 1)
                .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0));
        return panelPrefer;
    }

    // METHODS //
    public void updateStopTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        table.clear();

        HashMap<String, RealtimeStopUpdate> realtimeTrips = new HashMap<>();
        for (RealtimeStopUpdate update : realtimeUpdates) {
            System.out.println("Adding realtime update for trip ID: " + update.getTripId());
            realtimeTrips.put(update.getTripId(), update);
        }

        for (StopTimeModel stopTime : stopTimes) {
            TripModel trip = stopTime.getTrip(); // Corsa
            if (trip == null)
                continue;

            RouteModel route = trip.getRoute(); // Linea
            if (route == null || route.getShortName() == null) {
                continue;
            }

            int currentTime = LocalTime.now().toSecondOfDay();
            int staticArrivalTime = stopTime.getArrivalTime();
            int remainingMinutes = (staticArrivalTime - currentTime) / 60;

            RealtimeStopUpdate timeUpdate = realtimeTrips.get(trip.getId());
            String state = "Programmato";
            String occupancyStatus = "N/A";
            if (timeUpdate != null) {
                System.out.println("Found realtime update for trip ID: " + timeUpdate.getTripId() + " for stop "
                        + timeUpdate.getStopId());

                state = "In Arrivo";
                remainingMinutes = (timeUpdate.getArrivalTime() - currentTime) / 60;
                occupancyStatus = timeUpdate.getVehiclePosition().getOccupancyStatus().name();
            }

            table.addRow(new Object[] {
                    route.getShortName(),
                    trip.getHeadsign(),
                    stopTime.getArrivalTimeAsStr(),
                    state,
                    remainingMinutes >= 0 ? remainingMinutes : "N/A",
                    occupancyStatus,
                    route.getType()
                    // TODO: add "String vehicleId = timeUpdate.getVehiclePosition().getVehicle().getId(); // ID del veicolo fisico"
                    //  inside an invisile column to get the vehicle ID for row clicking and zooming on the map!
            });
            System.out.println("route type: " + route.getType());
        }
    }

    public void updateStopInfo(String id, String nome) {
        this.nomeFermata.setTextField(nome);
        this.idFermata.setTextField(id);
    }

    public void updateFavRouteText(String string) {
        favoriteRouteButton.setText(string);
        favoriteRouteButton.setEnabled(false);
    }

    public void updateFavButtons(boolean isFavorite, String stopId) {
        favoriteStopButton.update(isFavorite);
        favoriteStopButton.setItemId(stopId);
        favoriteRouteButton.reset();
    }

    public void updatePreferStopButton(boolean isFavorite, String stopId) {
        favoriteStopButton.setFavorite(isFavorite);
        favoriteStopButton.setItemId(stopId);
        favoriteStopButton.setEnabled(true);
    }

    public void updatePreferRouteButton(boolean isFavorite, String routeId) {
        favoriteRouteButton.setFavorite(isFavorite);
        favoriteRouteButton.setItemId(routeId);
        favoriteRouteButton.setEnabled(true);
    }

    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    // GETTERS AND SETTERS //
    public CustomPreferButton getFavoriteStopButton() {
        return favoriteStopButton;
    }

    public CustomPreferButton getFavoriteRouteButton() {
        return favoriteRouteButton;
    }

    public String getStopId() {
        return idFermata.getTextField();
    }

    // LISTENERS METHODS //
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

    public void setStopTimeRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        if (tableRowClickBehaviour != null) {
            this.table.setRowClickBehaviour(tableRowClickBehaviour);
        }
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour tableCheckIsFavBehaviour) {
    if (tableCheckIsFavBehaviour != null) {
            this.table.setTableCheckIsFavBehaviour(tableCheckIsFavBehaviour);
        }
    }
}
