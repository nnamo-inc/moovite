package com.nnamo.view.components;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.UpdateMode;
import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomSearchBar;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomGbc;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.*;

public class PreferPanel extends JPanel {

    // ATTRIBUTES //
    CustomSearchBar searchBar;
    CustomTable stopTable;
    CustomTable routeTable;

    // CONSTRUCTOR //
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
        JLabel titleLabel = new JLabel("Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));
    }

    private void createSearchBar() {
        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (RouteType type : RouteType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == RouteType.ALL); // Default selected type
            buttons.add(button);
        }
        searchBar = new CustomSearchBar(buttons);

        searchBar = new CustomSearchBar(buttons);
        add(searchBar, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));
    }

    private void createStopTable() {
        stopTable = new CustomTable(new ColumnName[] { NOME, CODICE },
                STOP);
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        stopTable.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(stopTable, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
    }

    private void createRouteTable() {
        routeTable = new CustomTable(new ColumnName[] { LINEA, CODICE, TIPO, CAPOLINEA, DIREZIONE },
                new ColumnName[] { DIREZIONE },
                ROUTE);
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        routeTable.setBorder(BorderFactory.createCompoundBorder(tableRouteBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(routeTable, new CustomGbc().setPosition(0, 3).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

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
                stopTable.removeRow(stop.getId(), ColumnName.CODICE);
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
                    routeTable.removeRow(route.getId(), ColumnName.CODICE);
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
