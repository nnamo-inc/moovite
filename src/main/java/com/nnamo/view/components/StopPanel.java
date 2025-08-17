package com.nnamo.view.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nnamo.enums.ColumnName;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomInfoBar;
import com.nnamo.view.customcomponents.CustomTable;
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
                new ColumnName[] { LINEA, DIREZIONE, ORARIO, STATO, MINUTIRIMAMENTI, POSTIDISPONIBILI, TIPO, TRIP },
                ROUTE);
        // Table
        mainPanel.add(tableTime, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        return mainPanel;
    }

    private JPanel newRouteInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        TitledBorder titledBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Tabella Linee");
        mainPanel.setBorder(titledBorder);

        this.tableService = new CustomTable(
                new ColumnName[] { LINEA, CODICE, TIPO, CAPOLINEA, DIREZIONE, INFORMAZIONI },
                new ColumnName[] { DIREZIONE },
                new ColumnName[] { LINEA },
                ROUTE);
        // Table
        mainPanel.add(tableService, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        return mainPanel;
    }

    // METHODS //
    public void updateStopTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        tableTime.clear();

        HashMap<String, RealtimeStopUpdate> realtimeTrips = new HashMap<>();
        for (RealtimeStopUpdate update : realtimeUpdates) {
            System.out.println("Adding realtime update for trip ID: " + update.getTripId());
            realtimeTrips.put(update.getTripId(), update);
        }

        List<Object[]> tableRows = new ArrayList<>();

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

            tableRows.add(new Object[] {
                    route.getShortName(),
                    trip.getHeadsign(),
                    stopTime.getArrivalTimeAsStr(),
                    state,
                    remainingMinutes >= 0 ? remainingMinutes : "N/A",
                    occupancyStatus,
                    route.getType().toString(),
                    trip.getId()
            });
        }

        // Sort by remaining minutes (column index 4)
        tableRows.sort((row1, row2) -> {
            Object min1 = row1[4];
            Object min2 = row2[4];

            // Handle "N/A" values - put them at the end
            if ("N/A".equals(min1) && "N/A".equals(min2))
                return 0;
            if ("N/A".equals(min1))
                return 1;
            if ("N/A".equals(min2))
                return -1;

            return Integer.compare((Integer) min1, (Integer) min2);
        });

        // Add sorted rows to table
        for (Object[] row : tableRows) {
            tableTime.addRow(row);
        }
    }

    public void updateStopRoutes(List<List<String>> uniqueRoutes) {
        tableService.clear();

        for (List<String> routeData : uniqueRoutes) {
            tableService.addRow(routeData.toArray());
        }
    }

    public void updateStopInfo(String id, String nome) {
        this.nomeFermata.setTextField(nome);
        this.idFermata.setTextField(id);
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

    public CustomTable getTimeTable() {
        return tableTime;
    }

    // LISTENERS METHODS //
    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.tableService.setTableRowClickBehaviour(listener);
        this.tableTime.setTableRowClickBehaviour(listener);
    }

    public void setStopInfoRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        if (tableRowClickBehaviour != null) {
            this.tableService.setTableRowClickBehaviour(tableRowClickBehaviour);
        }
    }

    public void setStopRouteRowClickBehaviour(TableRowClickBehaviour tableRowClickBehaviour) {
        if (tableRowClickBehaviour != null) {
            this.tableTime.setTableRowClickBehaviour(tableRowClickBehaviour);
        }
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour tableCheckIsFavBehaviour) {
        if (tableCheckIsFavBehaviour != null) {
            this.tableService.setTableCheckIsFavBehaviour(tableCheckIsFavBehaviour);
            this.tableTime.setTableCheckIsFavBehaviour(tableCheckIsFavBehaviour);
        }
    }
}
