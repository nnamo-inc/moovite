package com.nnamo.view.custompanels;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.RouteType;
import com.nnamo.enums.UpdateMode;
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
import static com.nnamo.enums.DataType.ROUTE;
import static com.nnamo.enums.DataType.STOP;

/**
 * Custom {@link JPanel} that displays stop and route preferences with two
 * {@link CustomTable},
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @see JPanel
 * @see CustomSearchBar
 * @see CustomTable
 * @see SearchBarListener
 * @see TableRowClickBehaviour
 * @see StopModel
 * @see RouteDirection
 * @see UpdateMode
 */
public class PreferPanel extends JPanel {

    // ATTRIBUTES //
    private CustomTitle titleLabel;
    private CustomSearchBar searchBar;
    private CustomTable stopTable;
    private CustomTable routeTable;

    // CONSTRUCTOR //

    /**
     * Creates a {@link PreferPanel} with a title bar, a {@link CustomSearchBar}
     * for searching and filtering, and {@link CustomTable} for displaying favorite
     * stops and routes.
     *
     * @see JPanel
     * @see CustomSearchBar
     * @see CustomTable
     */
    public PreferPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Title Label
        createTitleBar();
        // Search Bar
        createSearchBar();
        // Stop Table
        createStopTable();
        // Route Table
        createRouteTable();

        setVisible(false);
    }

    // METHODS //
    private void createTitleBar() {
        titleLabel = new CustomTitle("Prefer");
        add(titleLabel, new CustomGbc().setPosition(0, 0)
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

        JLabel stopLabel = new JLabel("Prefer Stops");
        stopLabel.setFont(new CustomFont());
        stopPanel.add(stopLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.CENTER)
                .setInsets(4, 5, -3, 5));

        stopTable = new CustomTable.Builder()
                .setTableColumns(new ColumnName[] { STOPNAME, CODE })
                .setDataType(STOP)
                .build();
        stopTable.setOpaque(false);

        stopPanel.add(stopTable, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        stopPanel.setBorder(new CustomRoundedBorder(20, 1.7f));
        add(stopPanel, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(5, 2, 5, 2));
    }

    private void createRouteTable() {
        JPanel routePanel = new JPanel(new GridBagLayout());

        JLabel routeLabel = new JLabel("Prefer Routes");
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
                .setTableColumns(new ColumnName[] { ROUTENAME, CODE, TYPE, TERMINAL, DIRECTION })
                .setHiddenColumns(new ColumnName[] { DIRECTION })
                .setCustomRadioButtons(buttons)
                .setDataType(ROUTE)
                .build();

        routePanel.add(routeTable, new CustomGbc()
                .setPosition(0, 2)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        routePanel.setBorder(new CustomRoundedBorder(20, 1.7f));
        add(routePanel, new CustomGbc()
                .setPosition(0, 3)
                .setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5)
                .setInsets(5, 5, 5, 5));

    }

    public void initPreferTable(List<StopModel> stops, List<RouteDirection> directedRoutes) {
        for (StopModel stop : stops) {
            updateFavStopTable(stop, UpdateMode.ADD);
        }
        updateFavRouteTable(directedRoutes, UpdateMode.ADD);
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        switch (updateMode) {
            case ADD:
                stopTable.addRow(new Object[] { stop.getName(), stop.getId() });
                break;
            case REMOVE:
                stopTable.removeRow(stop.getId(), ColumnName.CODE);
                break;
        }
    }

    public void updateFavRouteTable(List<RouteDirection> directedRoutes, UpdateMode updateMode) {
        for (RouteDirection route : directedRoutes) {
            switch (updateMode) {
                case ADD:
                    routeTable.addRow(new Object[] {
                            route.getLongName() != null ? route.getLongName() : route.getShortName(),
                            route.getId(),
                            route.getType(),
                            route.getDirectionName(),
                            route.getDirection().name(),

                    });
                    break;
                case REMOVE:
                    routeTable.removeRow(route.getId(), ColumnName.CODE);
                    break;
            }
        }
    }

    // BEHAVIOUR METHODS //
    public void addSearchListener(SearchBarListener listener) {
        this.searchBar.addSearchListener(listener);
    }

    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.stopTable.setTableRowClickBehaviour(listener);
        this.routeTable.setTableRowClickBehaviour(listener);
    }

    // GETTERS AND SETTERS //
    public CustomTable getStopTable() {
        return stopTable;
    }

    public CustomTable getRouteTable() {
        return routeTable;
    }

}
