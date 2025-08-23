package com.nnamo.view.components;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.*;

/**
 * Custom {@link JPanel} that provides a search interface for stops and routes, featuring a {@link CustomSearchBar} and two {@link CustomTable} components for displaying search results.
 * Allows setting listeners for search actions and table row clicks via interfaces.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @author Davide Galilei
 *
 * @see JPanel
 * @see CustomSearchBar
 * @see CustomTable
 * @see SearchBarListener
 * @see TableRowClickBehaviour
 */
public class SearchPanel extends JPanel {

    // ATTRIBUTES //
    CustomTitle title;
    CustomSearchBar searchBar;
    CustomTable stopTable;
    CustomTable routeTable;
    JPanel routePanel;

    // CONSTRUCTOR //
    /**
     * Creates a {@link SearchPanel} with a title bar, a {@link CustomSearchBar} for filtering, and {@link CustomTable} for displaying stops and routes.
     *
     * @see JPanel
     * @see CustomSearchBar
     * @see CustomTable
     */
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        // Title
        createTitleBar();
        // search bar
        createSearchBar();
        // Stop table
        createStopTable();
        // Route table
        createRouteTable();

        setVisible(false);
    }

    // METHODS //
    private void createTitleBar() {
        title = new CustomTitle("Search");
        add(title, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 2, 5, 2));
    }

    private void createSearchBar() {
        searchBar = new CustomSearchBar();
        add(searchBar, new CustomGbc()
                .setPosition(0, 1)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(5, 2, 5, 2));
    }

    private void createStopTable() {
        JPanel stopPanel = new JPanel(new GridBagLayout());

        JLabel stopLabel = new JLabel("Stops");
        stopLabel.setFont(new CustomFont());
        stopPanel.add(stopLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(4, 5, -3, 5));

        stopTable = new CustomTable.Builder()
                .setTableColumns(new ColumnName[] {CODE, STOPNAME})
                .setDataType(STOP)
                .build();
        stopTable.setOpaque(false);

        stopPanel.add(stopTable, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(2, 5, 2, 5)
                .setWeight(1.0, 0.5));

        stopPanel.setBorder(new CustomRoundedBorder(20, 1.7f));
        add(stopPanel, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(5, 2, 5, 2));
    }

    private void createRouteTable() {
        JPanel routePanel = new JPanel(new GridBagLayout());
        this.routePanel = routePanel;

        JLabel routeLabel = new JLabel("Routes");
        routeLabel.setFont(new CustomFont());
        routePanel.add(routeLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(4, 5, -3, 5));

        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (RouteType type : RouteType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == RouteType.ALL); // Default selected type
            buttons.add(button);
        }

        routeTable = new CustomTable.Builder()
                .setTableColumns(new ColumnName[] {CODE, TYPE, TERMINAL, DIRECTION})
                .setHiddenColumns(new ColumnName[] {DIRECTION})
                .setCustomRadioButtons(buttons)
                .setDataType(ROUTE)
                .build();

        routePanel.add(routeTable, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        add(routePanel, new CustomGbc()
                .setPosition(0, 4)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(5, 5, 5, 5));

        routePanel.setBorder(new CustomRoundedBorder(20, 1.7f));
    }

    /**
     * Updates the tables with the provided lists of {@link StopModel} and {@link RouteDirection}.
     * Clears previous results and populates the tables with new data.
     *
     * @param stopModels the list of stops to display in the stop table
     * @param routeModels the list of route directions to display in the route table
     *
     * @see StopModel
     * @see RouteDirection
     * @see CustomTable
     */
    public void updateView(List<StopModel> stopModels, List<RouteDirection> routeModels) {
        stopTable.clear();
        routeTable.clear();

        for (StopModel stop : stopModels) {
            stopTable.addRow(new Object[] { stop.getId(), stop.getName() });
        }

        for (RouteDirection route : routeModels) {
            String shortName = route.getShortName() != null ? route.getShortName() : "";
            routeTable.addRow(new Object[] {
                    shortName,
                    route.getType().name(),
                    route.getDirectionName(),
                    route.getDirection().name(),
            });
        }
    }

    // METHODS BEHAVIOUR //
    /**
     * Adds a {@link SearchBarListener} to the {@link CustomSearchBar} to handle search actions.
     *
     * @param listener the implementation of {@link SearchBarListener} to handle search events
     *
     * @see SearchBarListener
     * @see CustomSearchBar
     */
    public void addSearchListener(SearchBarListener listener) {
        searchBar.addSearchListener(listener);
    }

    /**
     * Sets the {@link TableRowClickBehaviour} for both the stop and route tables.
     *
     * @param listener the implementation of {@link TableRowClickBehaviour} to handle row clicks
     *
     * @see TableRowClickBehaviour
     * @see CustomTable
     */
    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        stopTable.setTableRowClickBehaviour(listener);
        routeTable.setTableRowClickBehaviour(listener);
    }
}
