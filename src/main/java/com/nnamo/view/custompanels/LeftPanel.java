package com.nnamo.view.custompanels;

import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.RealtimeMetricModel;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.view.customcomponents.statistic.MetricCollector;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Custom {@link JPanel} that serves as the left panel of the application,
 * containing various sub-panels for searching, preferences, statistics, and
 * settings.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @see JPanel
 * @see SearchPanel
 * @see PreferPanel
 * @see StatisticsPanel
 * @see SettingsPanel
 */
public class LeftPanel extends JPanel {

    // ATTRIBUTES //
    private SearchPanel searchPanel;
    private PreferPanel preferPanel;
    private StatisticsPanel statsPanel;
    private SettingsPanel settingsPanel;

    private final JPanel modularPanel;
    private final ButtonPanel buttonPanel;

    // CONSTRUCTOR //

    /**
     * Creates a {@link LeftPanel} with a layout that includes a button panel
     * and a modular panel for displaying different sub-panels.
     *
     * @see JPanel
     * @see ButtonPanel
     * @see SearchPanel
     * @see PreferPanel
     * @see StatisticsPanel
     * @see SettingsPanel
     */
    public LeftPanel() {
        setLayout(new BorderLayout());

        buttonPanel = new ButtonPanel(new LinkedHashMap<>() {
            {
                searchPanel = new SearchPanel();
                put(searchPanel, new ImageIcon(
                        Objects.requireNonNull(getClass().getResource("/images/panels/search_small.png"))));

                preferPanel = new PreferPanel();
                put(preferPanel,
                        new ImageIcon(
                                Objects.requireNonNull(getClass().getResource("/images/panels/favorite_small.png"))));

                statsPanel = new StatisticsPanel();
                put(statsPanel,
                        new ImageIcon(
                                Objects.requireNonNull(getClass().getResource("/images/panels/statistics_small.png"))));

                settingsPanel = new SettingsPanel();
                put(settingsPanel,
                        new ImageIcon(
                                Objects.requireNonNull(getClass().getResource("/images/panels/setting_small.png"))));
            }
        });

        modularPanel = new JPanel(new BorderLayout());
        add(modularPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);
    }

    // METHODS //

    /**
     * Updates the modular panel to display the specified panel.
     * If the panel is set to be visible, it will be added to the modular panel.
     * Otherwise, it will be hidden.
     *
     * @param panel     The panel to display in the modular area.
     * @param isVisible Whether the panel should be visible or not.
     */
    public void updateModularPanel(JPanel panel, boolean isVisible) {
        for (Component comp : modularPanel.getComponents()) {
            comp.setVisible(false);
        }

        modularPanel.removeAll();

        if (isVisible) {
            modularPanel.add(panel, BorderLayout.CENTER);
            panel.setVisible(true);
        } else {
            panel.setVisible(false);
        }

        modularPanel.revalidate();
        modularPanel.repaint();
    }

    // GETTERS AND SETTERS //

    /**
     * Returns the {@link SearchPanel} instance contained in this LeftPanel.
     *
     * @return The SearchPanel instance.
     */
    public SearchPanel getSearchPanel() {
        return this.searchPanel;
    }

    /**
     * Returns the {@link PreferPanel} instance contained in this LeftPanel.
     *
     * @return The PreferPanel instance.
     */
    public ButtonPanel getButtonPanel() {
        return this.buttonPanel;
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.settingsPanel.setRealtimeStatus(status);
    }

    // BEHAVIOURS METHODS //

    /**
     * Sets the click behaviour for table rows in both the search and prefer panels.
     *
     * @param listener The listener to handle table row clicks.
     */
    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        searchPanel.setTableRowClickBehaviour(listener);
        preferPanel.setTableRowClickBehaviour(listener);
    }

    /**
     * Sets the button panel general behaviour.
     *
     * @param listener The listener to handle button panel actions.
     */
    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    /**
     * Sets the button panel preference behaviour.
     *
     * @param listener The listener to handle button panel preference actions.
     */
    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    /**
     * Sets the logout behaviour for the settings panel.
     *
     * @param behaviour The logout behaviour to set.
     */
    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        settingsPanel.setLogoutBehaviour(behaviour);
    }

    /**
     * Sets the listener for the realtime switch in the settings panel.
     *
     * @param listener The listener to handle realtime switch events.
     */
    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.settingsPanel.setRealtimeSwitchListener(listener);
    }

    // TRANSIT METHODS //

    /**
     * Updates the favorite stop table in the prefer panel with the specified stop
     * and update mode.
     *
     * @param stop       The stop to update in the table.
     * @param updateMode The mode of update (ADD or REMOVE).
     */
    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        preferPanel.updateFavStopTable(stop, updateMode);
    }

    /**
     * Updates the favorite route table in the prefer panel with the specified route
     * and update mode.
     *
     * @param route      The route to update in the table.
     * @param updateMode The mode of update (ADD or REMOVE).
     */
    public void updateFavRouteTable(List<RouteDirection> route, UpdateMode updateMode) {
        preferPanel.updateFavRouteTable(route, updateMode);
    }

    /**
     * Initializes the prefer panel's table with the provided stops and routes.
     *
     * @param stops  The list of stops to initialize in the prefer table.
     * @param routes The list of route directions to initialize in the prefer table.
     */
    public void initPreferPanelPreferTable(List<StopModel> stops, List<RouteDirection> routes) {
        preferPanel.initPreferTable(stops, routes);
    }

    /**
     * Sets the listener for search events in the search panel.
     *
     * @param listener The listener to handle search events.
     */
    public void setSearchPanelListener(SearchBarListener listener) {
        searchPanel.addSearchListener(listener);
    }

    /**
     * Sets the listener for search events in the prefer panel.
     *
     * @param listener The listener to handle search events.
     */
    public void setPreferPanelListener(SearchBarListener listener) {
        preferPanel.addSearchListener(listener);
    }

    /**
     * Sets up the statistics panel with the provided realtime service and database
     * service.
     *
     * @param realtimeService The realtime GTFS service to use for statistics.
     * @param metricsMap      A map of realtime metric types to their corresponding metric models.
     * @param collector       The metric collector to gather and process metrics.
     */
    public void setupStatisticsPanel(RealtimeGtfsService realtimeService, Map<RealtimeMetricType, List<RealtimeMetricModel>> metricsMap, MetricCollector collector) {
        statsPanel.setupListeners(realtimeService);
        statsPanel.updateView(metricsMap);
        statsPanel.setMetricCollector(collector);
    }

    /**
     * Clears the tables in the prefer panel.
     */
    public void clearPreferPanelTable() {
        preferPanel.getStopTable().clear();
        preferPanel.getRouteTable().clear();
    }

    /**
     * Initializes the prefer panel's table with the provided favorite stops and
     * routes.
     *
     * @param favoriteStops  The list of favorite stops to initialize in the prefer
     *                       table.
     * @param favoriteRoutes The list of favorite route directions to initialize in
     *                       the prefer table.
     */
    public void initPreferTable(List<StopModel> favoriteStops, List<RouteDirection> favoriteRoutes) {
        preferPanel.initPreferTable(favoriteStops, favoriteRoutes);
    }

    /**
     * Updates the search panel with the provided list of stops and routes.
     *
     * @param stops  The list of stops to display in the search panel.
     * @param routes The list of route directions to display in the search panel.
     */
    public void renderSearchPanel(List<StopModel> stops, List<RouteDirection> routes) {
        searchPanel.updateView(stops, routes);
    }
}
