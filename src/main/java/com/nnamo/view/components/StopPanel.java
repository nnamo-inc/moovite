package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

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
import java.util.*;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.*;

public class StopPanel extends JPanel {
    // Stop info components
    private final CustomInfoBar nomeFermata = new CustomInfoBar("Nome fermata: ");
    private final CustomInfoBar idFermata = new CustomInfoBar("ID fermata: ");
    // Route info components
    private CustomTable tableTime;
    // Route service components
    private CustomTable tableService;
    // Prefer components
    private final CustomPreferButton favoriteStopButton = new CustomPreferButton("Fermata");
    private final CustomPreferButton favoriteRouteButton = new CustomPreferButton("Linea");

    // CONSTRUCTOR //
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 2));
        setBackground(new Color(60, 63, 65));

        // Stop info panel
        JPanel stopInfoPanel = newStopInfoPanel();
        add(stopInfoPanel, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.1).setWidth(3)
                .setInsets(2, 5, 2, 5));

        // Route service panel
        JPanel routeInfoPanel = newRouteInfoPanel();
        add(routeInfoPanel, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(0.5, 1.0)
                .setInsets(2, 5, 2, 5));

        // Route info panel
        JPanel routeTimePanel = newRouteTimePanel();
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella corse");
        routeTimePanel.setBorder(titledBorder);
        add(routeTimePanel,
                new CustomGbc().setPosition(1, 1).setAnchor(GridBagConstraints.WEST)
                        .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
                        .setInsets(2, 5, 2, 5));

        /*
         * // Buttons prefer
         * JPanel PanelPrefer = newPanelPrefer();
         * add(PanelPrefer,
         * new CustomGbc().setPosition(2, 1).setAnchor(GridBagConstraints.EAST)
         * .setFill(GridBagConstraints.BOTH).setWeight(0.1, 0.1)
         * .setInsets(2, 5, 2, 5));
         */

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

    private JPanel newRouteTimePanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        this.tableTime = new CustomTable(
                new ColumnName[] { LINEA, DIREZIONE, ORARIO, STATO, MINUTIRIMAMENTI, POSTIDISPONIBILI, TIPO }, ROUTE);
        tableTime.setSearchColumns(LINEA, DIREZIONE, ORARIO);
        // Table
        mainPanel.add(tableTime, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        return mainPanel;
    }

    private JPanel newRouteInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella Linee");
        mainPanel.setBorder(titledBorder);

        this.tableService = new CustomTable(new ColumnName[] { LINEA, DIREZIONE, TIPO }, ROUTE);
        tableService.setSearchColumns(LINEA, DIREZIONE);
        // Table
        mainPanel.add(tableService, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        return mainPanel;
    }

    /*
     * private JPanel newPanelPrefer() {
     * 
     * JPanel panelPrefer = new JPanel(new GridBagLayout());
     * TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray,
     * 2), "Preferiti");
     * panelPrefer.setBorder(titledBorder);
     * 
     * Dimension minButtonSize = new Dimension(100, Integer.MAX_VALUE);
     * favoriteStopButton.setMinimumSize(minButtonSize);
     * favoriteRouteButton.setMinimumSize(minButtonSize);
     * // button prefer
     * panelPrefer.add(favoriteStopButton, new CustomGbc().setPosition(0, 0)
     * .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.CENTER)
     * .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0));
     * 
     * favoriteRouteButton.setEnabled(false);
     * panelPrefer.add(favoriteRouteButton, new CustomGbc().setPosition(0, 1)
     * .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.CENTER)
     * .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0));
     * return panelPrefer;
     * }
     */

    // METHODS //
    public void updateStopTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        tableTime.clear();

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

            tableTime.addRow(new Object[] {
                    route.getShortName(),
                    trip.getHeadsign(),
                    stopTime.getArrivalTimeAsStr(),
                    state,
                    remainingMinutes >= 0 ? remainingMinutes : "N/A",
                    occupancyStatus,
                    route.getType()
                    // TODO: add "String vehicleId =
                    // timeUpdate.getVehiclePosition().getVehicle().getId(); // ID del veicolo
                    // fisico"
                    // inside an invisile column to get the vehicle ID for row clicking and zooming
                    // on the map!
            });
        }
    }

    public void updateStopRoutes(List<StopTimeModel> stopTimes) {
        tableService.clear();
        HashSet<List<String>> uniqueRoutes = new HashSet<>();

        for (StopTimeModel stopTime : stopTimes) {
            TripModel trip = stopTime.getTrip();
            trip.getDirection();
            RouteModel route = trip.getRoute();
            List<String> routeInfo = Arrays.asList(route.getShortName(), trip.getHeadsign(),
                    route.getType().toString());
            uniqueRoutes.add(routeInfo);
        }

        for (List<String> routeInfo : uniqueRoutes) {
            tableService.addRow(routeInfo.toArray());
        }
    }

    public void updateStopInfo(String id, String nome) {
        this.nomeFermata.setTextField(nome);
        this.idFermata.setTextField(id);
    }

    public void updateFavButtons(boolean isFavorite, String stopId) {
        favoriteStopButton.update(isFavorite);
        favoriteStopButton.setItemId(stopId);
    }

    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    // GETTERS AND SETTERS //
    public String getStopId() {
        return idFermata.getTextField();
    }

    // LISTENERS METHODS //
    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.tableService.setRowClickBehaviour(listener);
        this.tableTime.setRowClickBehaviour(listener);
    }

    public void setStopInfoRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        if (tableRowClickBehaviour != null) {
            this.tableService.setRowClickBehaviour(tableRowClickBehaviour);
        }
    }

    public void setStopRouteRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        if (tableRowClickBehaviour != null) {
            this.tableTime.setRowClickBehaviour(tableRowClickBehaviour);
        }
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour tableCheckIsFavBehaviour) {
        if (tableCheckIsFavBehaviour != null) {
            this.tableService.setTableCheckIsFavBehaviour(tableCheckIsFavBehaviour);
            this.tableTime.setTableCheckIsFavBehaviour(tableCheckIsFavBehaviour);
        }
    }
}
