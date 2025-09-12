package com.nnamo.view.custompanels;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopTimeModel;
import com.nnamo.models.TripModel;
import com.nnamo.utils.Log;
import com.nnamo.view.customcomponents.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.ROUTE;

/**
 * Custom {@link JPanel} that displays detailed information about a transit stop,
 * including stop info, route services, and upcoming departures.*
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @see JPanel
 * @see CustomInfoBar
 * @see CustomTable
 * @see TableRowClickBehaviour
 * @see TableCheckIsFavBehaviour
 */
public class StopPanel extends JPanel {
    // Stop info components
    private CustomInfoBar stopName;
    private CustomInfoBar stopCode;
    // Route info components
    private CustomTable tableTime;
    // Route service components
    private CustomTable tableService;
    // CONSTRUCTOR //

    /**
     * Creates a {@link StopPanel} with {@link CustomInfoBar} for stop information, route services, and upcoming departures.
     *
     * @see JPanel
     * @see CustomInfoBar
     * @see CustomTable
     * @see TableRowClickBehaviour
     * @see TableCheckIsFavBehaviour
     */
    public StopPanel() {
        super();
        // set the layout, create the border and the gbc, set the background color
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        // Stop info Panel
        createStopInfoPanel();
        // Route Time panel
        createRouteTimePanel();
        // Route Info panel
        createRouteInfoPanel();

        setVisible(false);
    }

    // COMPONENT METHODS //

    private void createStopInfoPanel() {
        JPanel stopInfoPanel = new JPanel(new GridBagLayout());
        JPanel stopInfo = new JPanel(new GridBagLayout());
        stopInfo.setBorder(new CustomRoundedBorder(20, 1.7f));

        JLabel infoLabel = new JLabel("Stop Info");
        infoLabel.setFont(new CustomFont());
        stopInfo.add(infoLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(0.0, 0.0)
                .setFill(GridBagConstraints.NONE)
                .setInsets(2, 5, -2, 5));


        stopName = new CustomInfoBar("Stop name: ");
        stopName.setOpaque(false);
        stopInfo.add(stopName, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 0, 5));

        stopCode = new CustomInfoBar("Stop code: ");
        stopCode.setOpaque(false);
        stopInfo.add(stopCode, new CustomGbc()
                .setPosition(0, 2)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(0, 5, 2, 5));

        stopInfoPanel.add(stopInfo, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 2, 5));

        add(stopInfoPanel, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(0.3, 0.0)
                .setFill(GridBagConstraints.BOTH));
    }

    private void createRouteInfoPanel() {

        JPanel routeInfoPanel = new JPanel(new GridBagLayout());
        routeInfoPanel.setBorder(new CustomRoundedBorder(20, 1.7f));

        JLabel infoLabel = new JLabel("Route Info");
        infoLabel.setFont(new CustomFont());
        routeInfoPanel.add(infoLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.NONE)
                .setInsets(2, 5, 0, 5));

        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (RouteType type : RouteType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == RouteType.ALL); // Default selected type
            buttons.add(button);
        }

        tableService = new CustomTable.Builder()
                .setTableColumns(new ColumnName[]{ROUTENAME, CODE, TYPE, TERMINAL, DIRECTION, INFORMATION})
                .setHiddenColumns(new ColumnName[]{DIRECTION})
//                .setSearchColumns(new ColumnName[] { ROUTENAME, CODE })
                .setCustomRadioButtons(buttons)
                .setDataType(ROUTE)
                .build();
//        tableService.getRadioButtonsPanel().setBorder(new EmptyBorder(0, 0, 0, 0));
//        tableService.getSearchBar().setBorder(new EmptyBorder(0, 0, 0, 0));

        routeInfoPanel.add(tableService, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(0, 5, 2, 5));

        add(routeInfoPanel, new CustomGbc()
                .setPosition(0, 2)
                .setAnchor(GridBagConstraints.CENTER)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(0.3, 1.0)
                .setInsets(5, 5, 5, 5));
    }

    private void createRouteTimePanel() {
        JPanel routeTimePanel = new JPanel(new GridBagLayout());
        routeTimePanel.setBorder(new CustomRoundedBorder(20, 1.7f));

        JLabel timeLabel = new JLabel("Time Routes Arrivals");
        timeLabel.setFont(new CustomFont());
        routeTimePanel.add(timeLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.NONE)
                .setInsets(2, 5, -3, 5));

        tableTime = new CustomTable.Builder()
                .setTableColumns(new ColumnName[]{ROUTENAME, DIRECTION, TIME, STATE, MINUTESLEFT, AVAILABLESEATS, TYPE, TRIP})
                .setHiddenColumns(new ColumnName[]{TRIP})
                .setDataType(ROUTE)
                .build();

        routeTimePanel.add(tableTime, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(2, 5, 2, 5));

        add(routeTimePanel, new CustomGbc()
                .setPosition(1, 0)
                .setAnchor(GridBagConstraints.WEST)
                .setFill(GridBagConstraints.BOTH)
                .setHeight(3)
                .setWeight(1.0, 1.0)
                .setInsets(5, 5, 5, 5));
    }
    // METHODS //

    /**
     * Updates the departures table with a list of {@link StopTimeModel} and their associated {@link RealtimeStopUpdate} data.
     * Sorts the {@link CustomTable} by remaining minutes and displays real-time status and occupancy if available.
     *
     * @param stopTimes       the list of scheduled stop times to display
     * @param realtimeUpdates the list of real-time updates for the stop times
     * @see StopTimeModel
     * @see RealtimeStopUpdate
     * @see CustomTable
     */
    public void updateStopTimes(List<StopTimeModel> stopTimes, List<RealtimeStopUpdate> realtimeUpdates) {
        tableTime.clear();

        HashMap<String, RealtimeStopUpdate> realtimeTrips = new HashMap<>();
        for (RealtimeStopUpdate update : realtimeUpdates) {
            Log.debug("Adding realtime update for trip ID: " + update.getTripId());
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
                Log.debug("Found realtime update for trip ID: " + timeUpdate.getTripId() + " for stop " + timeUpdate.getStopId());

                state = "In Arrivo";
                remainingMinutes = (timeUpdate.getArrivalTime() - currentTime) / 60;
                occupancyStatus = timeUpdate.getVehiclePosition().getOccupancyStatus().name();
            }

            tableRows.add(new Object[]{
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

    /**
     * Updates the route services table with a list of unique routes serving the stop.
     *
     * @param uniqueRoutes a list of route data, where each inner list represents a route's attributes
     * @see CustomTable
     */
    public void updateStopRoutes(List<List<String>> uniqueRoutes) {
        tableService.clear();

        for (List<String> routeData : uniqueRoutes) {
            tableService.addRow(routeData.toArray());
        }
    }

    /**
     * Updates the stop information section with the specified stop ID and name.
     *
     * @param id   the stop ID to display
     * @param nome the stop name to display
     * @see CustomInfoBar
     */
    public void updateStopInfo(String id, String nome) {
        this.stopName.setTextField(nome);
        this.stopCode.setTextField(id);
    }

    /**
     * Makes the {@link StopPanel} visible.
     *
     * @see JPanel
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Hides the {@link StopPanel}.
     *
     * @see JPanel
     */
    public void close() {
        setVisible(false);
    }

    // GETTERS AND SETTERS //

    /**
     * Returns the table displaying upcoming departures.
     *
     * @return the {@link CustomTable} for departures
     * @see CustomTable
     */
    public CustomTable getTimeTable() {
        return tableTime;
    }

    // LISTENERS METHODS //

    /**
     * Sets a generic row click behavior for both the route services and departures tables.
     *
     * @param listener the implementation of {@link TableRowClickBehaviour} to handle row clicks
     * @see TableRowClickBehaviour
     * @see CustomTable
     */
    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.tableService.setTableRowClickBehaviour(listener);
        this.tableTime.setTableRowClickBehaviour(listener);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test StopPanel");
        frame.setLayout(new BorderLayout());
        StopPanel stopPanel = new StopPanel();
        stopPanel.setVisible(true);
        frame.add(stopPanel, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
